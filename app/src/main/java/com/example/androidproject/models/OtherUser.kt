package com.example.androidproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "other_user")
@Serializable
class OtherUser(
    @PrimaryKey override var id: String = "",
    var email: String = "",
    var fullName: String = "",
    var phone :String = "",
    var image: String = "",
    var address: String = "",
    override var updatedAt: Long = System.currentTimeMillis(),
    // collections
    var posts: MutableList<String> = mutableListOf(),
    var favoritePosts: MutableList<String> = mutableListOf(),
    var ratedPosts: MutableList<String> = mutableListOf(),
) : BaseDocument()