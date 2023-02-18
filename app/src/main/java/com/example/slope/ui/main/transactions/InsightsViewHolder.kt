package com.example.slope.ui.main.transactions

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.slope.databinding.InsightItemBinding

internal sealed class InsightsViewHolder(bindings: ViewBinding) :
    RecyclerView.ViewHolder(bindings.root) {


    class InsightCardViewHolder(
        private val bindings: InsightItemBinding,
    ) : InsightsViewHolder(bindings) {
        fun bind(
            item: InsightsViewItem.Insight
        ) {
            bindings.insightType.text = item.insightType
            bindings.insightDetail.text = item.insightDetail
            bindings.amount.text = item.amount
            bindings.amount.setTextColor(
                ContextCompat.getColor(
                    bindings.root.context,
                    item.amountColor
                )
            )
        }
    }
}