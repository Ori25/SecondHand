package com.example.androidproject.helpers.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map

fun <T : Any> LiveData<T?>.observeNotNull(
    owner: LifecycleOwner,
    observer: Observer<in T>,
) {
    observe(owner) {
        if (it == null) return@observe
        observer.onChanged(it)
    }
}

fun <T> LiveData<T?>.observeOnlyNull(
    owner: LifecycleOwner,
    observer: Observer<in T?>,
) {
    observe(owner) {
        if (it != null) return@observe
        observer.onChanged(null)
    }
}

fun <T> LiveData<List<T>>.nullIfEmpty(): LiveData<List<T>?> {
    return map { list ->
        list.takeIf {
            it.isNotEmpty()
        }
    }
}