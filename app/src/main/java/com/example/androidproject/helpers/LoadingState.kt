package com.example.androidproject.helpers

sealed class LoadingState {
    data object Loaded : LoadingState()
    data object Loading : LoadingState()
}