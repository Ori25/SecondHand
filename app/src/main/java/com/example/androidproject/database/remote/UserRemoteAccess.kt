package com.example.androidproject.database.remote

import android.content.SharedPreferences
import android.net.Uri
import com.example.androidproject.database.common.AuthRepo
import com.example.androidproject.database.exceptions.InvalidStateException
import com.example.androidproject.dto.UserLoginForm
import com.example.androidproject.dto.UserRegistrationForm
import com.example.androidproject.helpers.extensions.getAsync
import com.example.androidproject.helpers.extensions.listen
import com.example.androidproject.helpers.extensions.listenAll
import com.example.androidproject.helpers.extensions.putAsync
import com.example.androidproject.helpers.extensions.setAsync
import com.example.androidproject.models.OtherUser
import com.example.androidproject.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.jvm.Throws

class UserRemoteAccess(val sp: SharedPreferences) : AuthRepo {

    companion object {
        const val USER_COLLECTION = "users"
    }

    fun listenAllUsers(
        lastUpdateKey: String,
        callback: (List<OtherUser>) -> Unit,
    ): ListenerRegistration {
        val lastUpdateDate = sp.getLong(lastUpdateKey, 0)
        return FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .listenAll(lastUpdateDate) { list: List<OtherUser> ->
                sp.edit()
                    .putLong(lastUpdateKey, System.currentTimeMillis())
                    .apply()
                callback.invoke(list)
            }
    }



    @Throws(InvalidStateException::class)
    fun listenToCurrentUser(callback: (User) -> Unit): ListenerRegistration {
        val id =
            FirebaseAuth.getInstance().uid ?: throw InvalidStateException("User is not connected")

        return FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(id)
            .listen(callback)
    }

    @Throws(Exception::class)
    override suspend fun updateUser(user: User, imageUri: Uri?) = withContext(Dispatchers.IO) {
        user.updatedAt = System.currentTimeMillis()
        user.apply {
            image = imageUri?.let { imageUriToUpload ->
                uploadImage("userImages/${id}", imageUriToUpload)
            } ?: image
        }
        return@withContext FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(user.id)
            .setAsync(user)
    }

    @Throws(Exception::class)
    override suspend fun signUp(form: UserRegistrationForm) = withContext(Dispatchers.IO) {
        val authResult = createUserHelper(form)
        val user = User(
            id = authResult.user!!.uid,
            email = form.email,
            fullName = form.fullName,
            phone = form.phone
        ).apply {
            image = form.imageUri?.let { imageUriToUpload ->
                uploadImage("userImages/${id}", imageUriToUpload)
            } ?: User.DEFAULT_IMAGE
        }
        return@withContext FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(user.id)
            .setAsync(user)
    }


    @Throws(Exception::class)
    override suspend fun signIn(form: UserLoginForm): User = withContext(Dispatchers.IO) {
        val authResult = signInHelper(form)
        FirebaseFirestore.getInstance()
            .collection(USER_COLLECTION)
            .document(authResult.user!!.uid)
            .getAsync()
    }

    @Throws(Exception::class)
    private suspend fun signInHelper(form: UserLoginForm) = withContext(Dispatchers.IO) {
        val deferredValue = CompletableDeferred<AuthResult>()
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(form.email, form.password)
            .addOnSuccessListener { authResult ->
                deferredValue.complete(authResult)
            }
            .addOnFailureListener {
                deferredValue.completeExceptionally(it)
            }
        deferredValue.await()
    }

    @Throws(Exception::class)
    private suspend fun createUserHelper(form: UserRegistrationForm) = withContext(Dispatchers.IO) {
        val deferredValue = CompletableDeferred<AuthResult>()
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(form.email, form.password)
            .addOnSuccessListener { authResult ->
                deferredValue.complete(authResult)
            }
            .addOnFailureListener {
                deferredValue.completeExceptionally(it)
            }
        deferredValue.await()
    }

    @Throws(Exception::class)
    private suspend fun uploadImage(pathRef: String, uri: Uri): String {
        return FirebaseStorage.getInstance()
            .getReference(pathRef)
            .putAsync(uri)
    }
}