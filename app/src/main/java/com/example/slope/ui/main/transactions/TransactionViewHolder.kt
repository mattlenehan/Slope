package com.example.slope.ui.main.transactions

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import com.example.slope.R
import com.example.slope.databinding.DateHeaderViewItemBinding
import com.example.slope.databinding.EmptyViewItemBinding
import com.example.slope.databinding.SortViewItemBinding
import com.example.slope.databinding.TransactionViewItemBinding
import com.example.slope.ui.main.SortOption
import com.example.slope.ui.main.getDisplayName
import com.example.slope.ui.main.util.getDisplayString
import com.example.slope.ui.main.util.roundedCorners
import com.example.slope.ui.main.util.toCurrencyDisplayText
import okhttp3.internal.format
import java.text.DecimalFormat
import java.text.NumberFormat

internal sealed class TransactionViewHolder(bindings: ViewBinding) :
    RecyclerView.ViewHolder(bindings.root) {

    class TransactionListViewHolder(
        private val bindings: TransactionViewItemBinding,
    ) : TransactionViewHolder(bindings) {
        fun bind(
            item: TransactionViewItem.TransactionListItem,
            onClick: (Int, String) -> Unit
        ) {
            val resources = bindings.root.resources
            val transaction = item.transaction
            bindings.merchantName.text = transaction.merchantName
            bindings.category.text = transaction.merchantCategory
            bindings.netAmount.text = transaction.amount.toCurrencyDisplayText(resources)
            bindings.logo.load(transaction.logoUrl) {
                roundedCorners()
            }
            bindings.root.setOnClickListener {
                onClick(transaction.id, transaction.merchantName)
            }
        }
    }

    class DateHeaderViewHolder(
        private val bindings: DateHeaderViewItemBinding,
    ) : TransactionViewHolder(bindings) {
        fun bind(
            item: TransactionViewItem.DateHeader
        ) {
            bindings.displayDate.text = item.localDate.getDisplayString()
        }
    }

    class SortViewHolder(
        private val bindings: SortViewItemBinding,
    ) : TransactionViewHolder(bindings) {
        fun bind(
            item: TransactionViewItem.SortItem,
            onNewSortOptionSelected: (SortOption) -> Unit
        ) {
            val context = bindings.root.context
            bindings.sortOptionText.text = item.selectedSortOption.getDisplayName(context)
            bindings.sortOptionText.setOnClickListener {
                showPopup(bindings.root, context, onNewSortOptionSelected)
            }
        }

        private fun showPopup(
            v: View,
            context: Context,
            onNewSortOptionSelected: (SortOption) -> Unit) {
            PopupMenu(context, v).apply {
                setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.newest_first -> {
                            onNewSortOptionSelected(SortOption.DATE_NEWEST)
                        }
                        R.id.oldest_first -> {
                            onNewSortOptionSelected(SortOption.DATE_OLDEST)
                        }
                        R.id.merchant -> {
                            onNewSortOptionSelected(SortOption.MERCHANT)
                        }
                        R.id.amount -> {
                            onNewSortOptionSelected(SortOption.AMOUNT)
                        }
                    }
                    true
                }
                inflate(R.menu.sort_options_menu)
                show()
            }
        }
    }

    class EmptyViewHolder(
        private val bindings: EmptyViewItemBinding,
    ) : TransactionViewHolder(bindings) {}
}
