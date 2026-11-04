package com.example.shelflife.data

import androidx.room.TypeConverter
import java.util.Date

// Room can't store Date directly, so dates are saved as milliseconds (Long).
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
