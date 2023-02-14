package com.example.networking.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Moshi serialization of LocalDate. Uses the format "2021-08-01 08:04:41"
 */
class LocalDateTimeAdapter {
    @ToJson
    fun toJson(value: LocalDateTime): String {
        return FORMATTER.format(value)
    }

    @FromJson
    fun fromJson(value: String): LocalDateTime {
        val accessor = FORMATTER.parse(value, LocalDateTime::from)
        return LocalDateTime.from(accessor)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
    }
}
