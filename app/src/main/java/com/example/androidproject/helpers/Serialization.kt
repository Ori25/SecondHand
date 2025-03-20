package com.example.androidproject.helpers

import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}