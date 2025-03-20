package com.example.androidproject.dto

import android.net.Uri

data class UserRegistrationForm(
    var fullName: String = "",
    var email: String = "",
    var address : String = "",
    var phone: String = "",
    var password: String = "",
    var imageUri: Uri? = null
)