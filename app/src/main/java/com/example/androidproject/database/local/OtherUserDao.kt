package com.example.androidproject.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidproject.models.OtherUser
import com.example.androidproject.models.User

@Dao
interface OtherUserDao {
    @Query("SELECT * from other_user ")
    fun listenAllUsers(): LiveData<List<OtherUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: OtherUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<OtherUser>)
}