package com.example.networking.json

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Moshi serialization of LocalDate. Uses the format "2021-08-22"
 */
class LocalDateAdapter {
    @RequiresApi(Build.VERSION_CODES.O)
    @ToJson
    fun toJson(value: LocalDate): String {
        return FORMATTER.format(value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @FromJson
    fun fromJson(value: String): LocalDate {
        val accessor = FORMATTER.parse(value, LocalDate::from)
        return LocalDate.from(accessor)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val FORMATTER = DateTimeFormatter.ISO_DATE
    }
}
