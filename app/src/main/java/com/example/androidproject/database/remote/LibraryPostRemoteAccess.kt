package com.example.androidproject.database.remote

import android.content.SharedPreferences
import android.net.Uri
import com.example.androidproject.database.common.PostRepo
import com.example.androidproject.helpers.extensions.create
import com.example.androidproject.helpers.extensions.listenAll
import com.example.androidproject.helpers.extensions.putAsync
import com.example.androidproject.helpers.extensions.setAsync
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage

class LibraryPostRemoteAccess(private val sp: SharedPreferences): PostRepo {

    companion object {
        const val POSTS_COLLECTION = "library_posts"
    }

    override suspend fun createPost(post: ItemPost, imageUri: Uri?): ItemPost {
        post.apply {
            image = imageUri?.let { imageUriToUpload ->
                uploadImage("postImages/${id}", imageUriToUpload)
            } ?: User.DEFAULT_IMAGE
        }
        return FirebaseFirestore
            .getInstance()
            .collection(POSTS_COLLECTION)
            .create(post)
    }

    override suspend fun deletePost(post: ItemPost): ItemPost {
        post.deleted = true
        return FirebaseFirestore
            .getInstance()
            .collection(POSTS_COLLECTION)
            .document(post.id)
            .setAsync(post)
    }

    override suspend fun updatePost(post: ItemPost, imageUri: Uri?): ItemPost {
        post.apply {
            image = imageUri?.let { imageUriToUpload ->
                uploadImage("postImages/${id}", imageUriToUpload)
            } ?: User.DEFAULT_IMAGE
        }
        return FirebaseFirestore
            .getInstance()
            .collection(POSTS_COLLECTION)
            .document(post.id)
            .setAsync(post)
    }

    fun listenAllPosts(
        lastUpdateKey: String,
        callback: (List<ItemPost>) -> Unit,
    ): ListenerRegistration {
        val lastUpdateDate = sp.getLong(lastUpdateKey, 0)
        return FirebaseFirestore
            .getInstance()
            .collection(POSTS_COLLECTION)
            .listenAll(lastUpdateDate) { list: List<ItemPost> ->
                sp.edit()
                    .putLong(lastUpdateKey, System.currentTimeMillis())
                    .apply()
                callback.invoke(list)
            }
    }

    private suspend fun uploadImage(pathRef: String, uri: Uri): String {
        return FirebaseStorage.getInstance()
            .getReference(pathRef)
            .putAsync(uri)
    }

}