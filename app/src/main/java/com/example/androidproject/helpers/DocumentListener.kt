package com.example.androidproject.helpers

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ListenerRegistration

data class DocumentListener<T>(
    val liveData: LiveData<T>, // listener on SSOT (LocalDB)
    val listenerRegistration: ListenerRegistration // listener registration on remote db
) {
    fun get(): LiveData<T> {
        return liveData
    }
    fun stopListening() {
        listenerRegistration.remove()
    }
}