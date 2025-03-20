package com.example.androidproject.di

import android.content.SharedPreferences
import com.example.androidproject.database.common.CurrentUserRepository
import com.example.androidproject.database.common.UserRepository
import com.example.androidproject.database.local.AppDatabase
import com.example.androidproject.database.local.CurrentUserDao
import com.example.androidproject.database.local.OtherUserDao
import com.example.androidproject.database.remote.UserRemoteAccess
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UsersModule {

    @Singleton
    @Provides
    fun provideCurrentUserDao(database: AppDatabase): CurrentUserDao {
        return database.currentUserDao()
    }

    @Singleton
    @Provides
    fun provideOtherUserDao(database: AppDatabase): OtherUserDao {
        return database.otherUserDao()
    }

    @Singleton
    @Provides
    fun provideUserRemoteAccess(sp: SharedPreferences): UserRemoteAccess {
        return UserRemoteAccess(sp)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        otherUserDao: OtherUserDao,
        remoteDb: UserRemoteAccess,
        coroutineScope: CoroutineScope,
    ): UserRepository {
        return UserRepository(otherUserDao, remoteDb, coroutineScope)
    }

    @Singleton
    @Provides
    fun provideCurrentUserRepository(
        currentUserDao: CurrentUserDao,
        remoteDb: UserRemoteAccess,
        coroutineScope: CoroutineScope,
    ): CurrentUserRepository {
        return CurrentUserRepository(currentUserDao, remoteDb, coroutineScope)
    }

}