package com.example.slope.ui.main.util

import android.content.res.Resources
import android.view.View
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.slope.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("eeee, MMM d")
    .withZone(ZoneId.systemDefault())
private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
    .withZone(ZoneId.systemDefault())

fun ImageRequest.Builder.roundedCorners() {
    crossfade(true)
    transformations(
        RoundedCornersTransformation(16f)
    )
}

fun View.showSnackbar(msg: String?) {
    if (msg != null) {
        Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
            .setAction("dismiss") {}
            .show()
    }
}

fun LocalDate.getDisplayString(): String {
    return DATE_FORMATTER.format(this)
}

fun LocalDateTime.getDisplayString(): String {
    return DATE_TIME_FORMATTER.format(this)
}

fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it]) {
        is JSONArray -> {
            val map = (0 until value.length()).associate { key ->
                Pair(key.toString(), value[key])
            }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else -> value
    }
}

fun Double.toCurrencyDisplayText(resources: Resources): String {
    val formatter: NumberFormat = DecimalFormat("#,###.##")
    val formattedAmount: String = formatter.format(this)
    return resources.getString(R.string.dollar_x, formattedAmount)
}

fun String.splitCamelCase(): String {
    return this.capitalize(Locale.current).replace(
        String.format(
            "%s|%s|%s",
            "(?<=[A-Z])(?=[A-Z][a-z])",
            "(?<=[^A-Z])(?=[A-Z])",
            "(?<=[A-Za-z])(?=[^A-Za-z])"
        ).toRegex(),
        " "
    )
}