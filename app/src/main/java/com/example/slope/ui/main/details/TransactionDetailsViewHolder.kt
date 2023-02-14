package com.example.slope.ui.main.details

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import com.example.slope.databinding.DetailsAttributeViewItemBinding
import com.example.slope.databinding.DetailsHeaderViewItemBinding
import com.example.slope.ui.main.util.getDisplayString
import com.example.slope.ui.main.util.roundedCorners
import com.example.slope.ui.main.util.toCurrencyDisplayText

internal sealed class TransactionDetailsViewHolder(bindings: ViewBinding) :
    RecyclerView.ViewHolder(bindings.root) {

    class HeaderViewHolder(
        private val bindings: DetailsHeaderViewItemBinding,
    ) : TransactionDetailsViewHolder(bindings) {
        fun bind(
            item: TransactionDetailsViewItem.Header
        ) {
            val resources = bindings.root.resources
            val transaction = item.transaction
            bindings.logo.load(transaction.logoUrl) {
                roundedCorners()
            }
            bindings.amount.text = transaction.amount.toCurrencyDisplayText(resources)
            bindings.date.text = transaction.date.getDisplayString()
        }
    }

    class AttributeViewHolder(
        private val bindings: DetailsAttributeViewItemBinding,
    ) : TransactionDetailsViewHolder(bindings) {
        fun bind(
            item: TransactionDetailsViewItem.Attribute
        ) {
            bindings.field.text = item.key
            bindings.value.text = item.value
        }
    }
}
