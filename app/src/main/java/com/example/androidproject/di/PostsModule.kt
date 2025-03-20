package com.example.androidproject.di

import android.content.SharedPreferences
import com.example.androidproject.database.common.CurrentUserRepository
import com.example.androidproject.database.common.LibraryPostRepository
import com.example.androidproject.database.common.UserRepository
import com.example.androidproject.database.local.AppDatabase
import com.example.androidproject.database.local.CurrentUserDao
import com.example.androidproject.database.local.LibraryPostDao
import com.example.androidproject.database.local.OtherUserDao
import com.example.androidproject.database.remote.LibraryPostRemoteAccess
import com.example.androidproject.database.remote.UserRemoteAccess
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PostsModule {

    @Singleton
    @Provides
    fun providePostsDao(database: AppDatabase): LibraryPostDao {
        return database.libraryPostsDao()
    }

    @Singleton
    @Provides
    fun provideLibraryPostRemoteAccess(sp: SharedPreferences): LibraryPostRemoteAccess {
        return LibraryPostRemoteAccess(sp)
    }

    @Singleton
    @Provides
    fun provideLibraryPostRepository(
        libraryPostsDao: LibraryPostDao,
        remoteDb: LibraryPostRemoteAccess,
        coroutineScope: CoroutineScope,
    ): LibraryPostRepository {
        return LibraryPostRepository(libraryPostsDao, remoteDb, coroutineScope)
    }

}