package com.example.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "id")
    val id: Int,

    @Json(name = "date")
    val date: LocalDateTime,

    @Json(name = "clearing_time")
    val clearingTime: String, // in the form of "2021-08-01 08:04:41" but could be empty String

    @Json(name = "amount")
    val amount: Double,

    @Json(name = "merchant_name")
    val merchantName: String,

    @Json(name = "merchant_city")
    val merchantCity: String,

    @Json(name = "merchant_state")
    val merchantState: String,

    @Json(name = "merchant_category")
    val merchantCategory: String,

    @Json(name = "card_last_4")
    val cardLast4: Int,

    @Json(name = "card_display_name")
    val cardDisplayName: String,

    @Json(name = "has_receipt")
    val hasReceipt: String,

    @Json(name = "accounting_sync_date")
    val accountingSyncDate: String,

    @Json(name = "logo_url")
    val logoUrl: String,
)