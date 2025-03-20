package com.example.androidproject.network

import com.example.androidproject.helpers.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure<T>(val exc: Exception) : Resource<T>()
}


class HttpClient {


    suspend inline fun <reified T> get(urlString: String): Resource<T> =
        withContext(Dispatchers.IO) {
            try {
                URL(urlString).openConnection()
                    .getInputStream()
                    .use {
                        Resource.Success(
                            json.decodeFromString(
                                it.bufferedReader()
                                    .readText()
                            )
                        )
                    }
            } catch (e: Exception) {
                Resource.Failure(e)
            }
        }
}