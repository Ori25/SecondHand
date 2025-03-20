package com.example.androidproject.database.local

import androidx.room.TypeConverter
import com.example.androidproject.helpers.json
import kotlinx.serialization.encodeToString

class Converters {
    @TypeConverter
    fun fromStringToListOfStrings(str: String): MutableList<String> {
            return json.decodeFromString(str)
    }
    @TypeConverter
    fun fromListOfStringsToString(list: MutableList<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToListOfFloats(str: String): MutableList<Float> {
        return json.decodeFromString(str)
    }
    @TypeConverter
    fun fromListOfFloatsToString(list: MutableList<Float>): String {
        return json.encodeToString(list)
    }
}