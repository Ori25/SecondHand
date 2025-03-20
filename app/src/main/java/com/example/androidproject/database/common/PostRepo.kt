package com.example.androidproject.database.common

import android.net.Uri
import com.example.androidproject.models.ItemPost
import kotlin.jvm.Throws

interface PostRepo {
    @Throws(Exception::class)
    suspend fun createPost(post: ItemPost, imageUri: Uri? = null): ItemPost
    @Throws(Exception::class)
    suspend fun updatePost(post: ItemPost, imageUri: Uri? = null): ItemPost
    @Throws(Exception::class)
    suspend fun deletePost(post: ItemPost): ItemPost
}