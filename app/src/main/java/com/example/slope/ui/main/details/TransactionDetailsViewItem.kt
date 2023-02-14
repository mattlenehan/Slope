package com.example.slope.ui.main.details

import com.example.models.Transaction
import com.example.slope.R
import com.example.slope.ui.main.SortOption
import com.example.slope.ui.main.util.ViewItem
import java.time.LocalDate

sealed class TransactionDetailsViewItem(
    open val id: String,
    open val type: TransactionDetailsViewItemType
) : ViewItem<TransactionDetailsViewItem> {

    override fun compareTo(other: TransactionDetailsViewItem): Int = this.id.compareTo(other.id)

    override fun areContentsTheSame(other: TransactionDetailsViewItem): Boolean = this == other

    override fun areItemsTheSame(other: TransactionDetailsViewItem): Boolean = type == other.type && id == other.id

    data class Header(
        override val id: String,
        val transaction: Transaction
    ) : TransactionDetailsViewItem(
        id = id,
        type = TransactionDetailsViewItemType.HEADER,
    )

    data class Attribute(
        override val id: String,
        val key: String,
        val value: String
    ) : TransactionDetailsViewItem(
        id = id,
        type = TransactionDetailsViewItemType.ATTRIBUTE
    )
}

enum class TransactionDetailsViewItemType(
    val layoutId: Int,
) {
    HEADER(R.layout.details_header_view_item),
    ATTRIBUTE(R.layout.details_attribute_view_item),
}

