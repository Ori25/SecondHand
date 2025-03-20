package com.example.androidproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "current_user")
@Serializable
open class User(
    @PrimaryKey override var id : String = "",
    var email: String = "",
    var fullName: String  = "",
    var image: String = "",
    var phone: String = "",
    var address: String = "",
    override var updatedAt: Long = System.currentTimeMillis(),
    // collections
    var posts: MutableList<String> = mutableListOf(),
    var favoritePosts: MutableList<String> = mutableListOf(),
    var ratedPosts: MutableList<String> = mutableListOf()
) : BaseDocument() {
    companion object {
        const val DEFAULT_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/100px-No_image_available.svg.png"
    }
}