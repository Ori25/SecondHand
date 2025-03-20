package com.example.androidproject.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidproject.models.ItemPost
import com.google.firebase.auth.FirebaseAuth

@Dao
interface LibraryPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<ItemPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: ItemPost)

    @Query("SELECT * from posts order by updatedAt DESC")
    fun listenAllPosts(): LiveData<List<ItemPost>>

    @Query("SELECT * from posts where owner = :uid order by updatedAt DESC")
    fun listenMyPosts(uid: String? = FirebaseAuth.getInstance().uid): LiveData<List<ItemPost>>


}