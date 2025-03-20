package com.example.androidproject.database.common

import android.net.Uri
import com.example.androidproject.dto.UserLoginForm
import com.example.androidproject.dto.UserRegistrationForm
import com.example.androidproject.models.User
import kotlin.jvm.Throws

interface AuthRepo {
    @Throws(Exception::class)
    suspend fun signIn(form: UserLoginForm): User

    @Throws(Exception::class)
    suspend fun signUp(form: UserRegistrationForm): User

    @Throws
    suspend fun updateUser(user: User, imageUri: Uri? = null): User
}