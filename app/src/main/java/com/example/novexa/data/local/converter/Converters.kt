package com.example.novexa.data.local.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.util.Date

/**
 * Type converters for Room Database.
 * Handles conversions for date/time and complex data structures (Lists, Maps).
 */
class Converters {

    // --- Date / Time Converters ---

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromEpochMilli(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToEpochMilli(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    // --- Complex Fields: List<String> (e.g. image galleries, tags) ---

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    // --- Complex Fields: Map<String, String> (e.g. product attributes, specifications) ---

    @TypeConverter
    fun fromStringMap(map: Map<String, String>?): String {
        if (map.isNullOrEmpty()) return ""
        return map.entries.joinToString(separator = ";") { "${it.key}:${it.value}" }
    }

    @TypeConverter
    fun toStringMap(data: String?): Map<String, String> {
        if (data.isNullOrBlank()) return emptyMap()
        return data.split(";")
            .mapNotNull { entry ->
                val parts = entry.split(":", limit = 2)
                if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
            }
            .toMap()
    }
}
