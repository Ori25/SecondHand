package com.example.androidproject.di

import android.content.Context
import android.content.SharedPreferences
import com.example.androidproject.database.local.AppDatabase
import com.example.androidproject.network.HttpClient
import com.example.androidproject.network.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("com.example.androidproject", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }



    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideHttpClient() = HttpClient()


    @Singleton
    @Provides
    fun provideWeatherService(httpClient: HttpClient): WeatherService {
        return WeatherService(httpClient)
    }
}