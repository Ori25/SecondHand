package com.example.androidproject.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidproject.models.User

@Dao
interface CurrentUserDao {
    @Query("SELECT * from current_user LIMIT 1")
    fun listenCurrentUser(): LiveData<User?>

    @Query("DELETE from current_user")
    suspend fun deleteCurrentUser() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

}