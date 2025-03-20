package com.example.androidproject.database.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.androidproject.database.local.CurrentUserDao
import com.example.androidproject.database.remote.UserRemoteAccess
import com.example.androidproject.helpers.DocumentListener
import com.example.androidproject.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CurrentUserRepository(
    private val localDb: CurrentUserDao,
    private val remoteDb: UserRemoteAccess,
    private val localDbOpsScope: CoroutineScope
) : AuthRepo by remoteDb {
    private var currentUserDocumentListener: DocumentListener<User?>? = null
    private val currentUserMediator = MediatorLiveData<User?>()

    val currentUser: LiveData<User?> get() = currentUserMediator

    private var authListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { authState ->
            if (authState.currentUser != null) { // user authenticated
                currentUserDocumentListener = listenCurrentUser()
                currentUserMediator.addSource(
                    currentUserDocumentListener!!.get(),
                    currentUserMediator::postValue
                )
            } else { // user logged out..
                localDbOpsScope.launch {
                    localDb.deleteCurrentUser()
                }
            }
        }

    fun startListening() {
        FirebaseAuth.getInstance()
            .addAuthStateListener(authListener)
    }

    fun stopListening() {
        if (currentUserDocumentListener != null) {
            currentUserDocumentListener?.stopListening()
        }
        FirebaseAuth.getInstance()
            .removeAuthStateListener(authListener)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun listenCurrentUser(): DocumentListener<User?> {
        return DocumentListener(
            localDb.listenCurrentUser(),
            remoteDb.listenToCurrentUser {
                localDbOpsScope.launch {
                    localDb.insert(it)
                }
            }
        )
    }
}