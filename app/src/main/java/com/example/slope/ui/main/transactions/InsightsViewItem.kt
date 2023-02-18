package com.example.slope.ui.main.transactions

import com.example.slope.R
import com.example.slope.ui.main.util.ViewItem

sealed class InsightsViewItem(
    open val id: String,
    open val type: InsightsViewItemType
) : ViewItem<InsightsViewItem> {

    override fun compareTo(other: InsightsViewItem): Int = this.id.compareTo(other.id)

    override fun areContentsTheSame(other: InsightsViewItem): Boolean = this == other

    override fun areItemsTheSame(other: InsightsViewItem): Boolean =
        type == other.type && id == other.id

    data class Insight(
        override val id: String,
        val insightType: String,
        val insightDetail: String? = null,
        val amount: String,
        val amountColor: Int
    ) : InsightsViewItem(
        id = id,
        type = InsightsViewItemType.INSIGHT
    )
}

enum class InsightsViewItemType(
    val layoutId: Int,
) {
    INSIGHT(R.layout.insight_item),
}
