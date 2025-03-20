package com.example.androidproject.helpers.extensions

import android.net.Uri
import com.example.androidproject.database.remote.UserRemoteAccess
import com.example.androidproject.models.BaseDocument
import com.example.androidproject.models.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception


const val LAST_UPDATE_KEY = "updatedAt"


suspend fun <T : BaseDocument> CollectionReference.create(doc: T) =
    withContext(Dispatchers.IO) {
        val ref = document()
        doc.id = ref.id
        ref.setAsync(doc)
    }

suspend fun StorageReference.putAsync(uri: Uri): String = withContext(Dispatchers.IO) {
    val deferredValue = CompletableDeferred<String>()
    putFile(uri)
        .addOnSuccessListener {
            downloadUrl.addOnSuccessListener { imageUri ->
                deferredValue.complete(imageUri.toString())
            }.addOnFailureListener {
                deferredValue.completeExceptionally(it)
            }
        }
        .addOnFailureListener {
            deferredValue.completeExceptionally(it)
        }
    deferredValue.await()
}

inline fun <reified T : BaseDocument> DocumentReference.listen(crossinline callback: (T) -> Unit): ListenerRegistration {
    return addSnapshotListener { value, _ ->
        value?.apply {
            toObject(T::class.java)?.let {
                callback.invoke(it)
            }
        }
    }
}

inline fun <reified T : BaseDocument> Query.listenAll(
    lastUpdateDate: Long,
    crossinline callback: (List<T>) -> Unit,
): ListenerRegistration {
    return whereGreaterThanOrEqualTo(LAST_UPDATE_KEY, lastUpdateDate - 10 * 60 * 1000)
        .addSnapshotListener { value, _ ->
            value?.apply {
                toObjects(T::class.java).let {
                    callback.invoke(it)
                }
            }
        }
}

suspend inline fun <reified T : BaseDocument> DocumentReference.getAsync() =
    withContext(Dispatchers.IO) {
        val deferredValue = CompletableDeferred<T>()
        get()
            .addOnSuccessListener {
                it.toObject(T::class.java)?.let { doc ->
                    deferredValue.complete(doc)
                } ?: run {
                    deferredValue.completeExceptionally(Exception("Unknown error occurred while fetching document"))
                }
            }
            .addOnFailureListener {
                deferredValue.completeExceptionally(it)
            }
        deferredValue.await()
    }

suspend fun <T : BaseDocument> DocumentReference.setAsync(value: T) = withContext(Dispatchers.IO) {
    val deferredValue = CompletableDeferred<T>()
    value.updatedAt = System.currentTimeMillis()
    set(value)
        .addOnSuccessListener {
            deferredValue.complete(value)
        }
        .addOnFailureListener {
            deferredValue.completeExceptionally(it)
        }
    deferredValue.await()
}

suspend fun DocumentReference.deleteAsync() = withContext(Dispatchers.IO) {
    val deferredValue = CompletableDeferred<Void>()
    delete()
        .addOnSuccessListener {
            deferredValue.complete(it)
        }
        .addOnFailureListener {
            deferredValue.completeExceptionally(it)
        }
    deferredValue.await()
}