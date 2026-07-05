package com.savia.camaguey.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter para almacenar List<String> en Room como JSON string.
 * Usado en Product.tags y Product.fotoUrls.
 */
class StringListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
