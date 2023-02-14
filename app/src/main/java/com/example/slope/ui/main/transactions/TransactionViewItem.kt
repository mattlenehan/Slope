package com.example.slope.ui.main.transactions

import com.example.models.Transaction
import com.example.slope.R
import com.example.slope.ui.main.SortOption
import com.example.slope.ui.main.util.ViewItem
import java.time.LocalDate

sealed class TransactionViewItem(
    open val id: String,
    open val type: TransactionViewItemType
) : ViewItem<TransactionViewItem> {

    override fun compareTo(other: TransactionViewItem): Int = this.id.compareTo(other.id)

    override fun areContentsTheSame(other: TransactionViewItem): Boolean = this == other

    override fun areItemsTheSame(other: TransactionViewItem): Boolean = type == other.type && id == other.id

    data class TransactionListItem(
        override val id: String,
        val transaction: Transaction
    ) : TransactionViewItem(
        id = id,
        type = TransactionViewItemType.TRANSACTION_LIST_ITEM,
    )

    data class DateHeader(
        override val id: String,
        val localDate: LocalDate
    ) : TransactionViewItem(
        id = id,
        type = TransactionViewItemType.DATE_HEADER
    )

    data class SortItem(
        override val id: String,
        val selectedSortOption: SortOption
    ) : TransactionViewItem(
        id = id,
        type = TransactionViewItemType.SORT_ITEM
    )

    data class EmptyState(
        override val id: String = TransactionViewItemType.EMPTY.toString(),
    ) : TransactionViewItem(
        id = id,
        type = TransactionViewItemType.EMPTY
    )
}

enum class TransactionViewItemType(
    val layoutId: Int,
) {
    TRANSACTION_LIST_ITEM(R.layout.transaction_view_item),
    DATE_HEADER(R.layout.date_header_view_item),
    SORT_ITEM(R.layout.sort_view_item),
    EMPTY(R.layout.empty_view_item)
}
