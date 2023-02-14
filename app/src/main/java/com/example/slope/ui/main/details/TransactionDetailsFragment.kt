package com.example.slope.ui.main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slope.R
import com.example.slope.databinding.DetailsAttributeViewItemBinding
import com.example.slope.databinding.DetailsHeaderViewItemBinding
import com.example.slope.databinding.FragmentTransactionDetailsBinding
import com.example.slope.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailsFragment : Fragment() {

    private val viewModel: MainViewModel
            by navGraphViewModels(R.id.nav_graph) {
                defaultViewModelProviderFactory
            }

    private val detailsAdapter by lazy {
        ListAdapter()
    }

    private val args: TransactionDetailsFragmentArgs by navArgs()

    private var _binding: FragmentTransactionDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        subscribeUi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        binding.recycler.apply {
            adapter = detailsAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    private fun subscribeUi() {
        viewModel.getTransactionDetails(args.transactionId)
        viewModel.details.observe(
            viewLifecycleOwner
        ) {
            detailsAdapter.accept(it ?: emptyList())
        }
    }
}

private class ListAdapter : RecyclerView.Adapter<TransactionDetailsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<TransactionDetailsViewItem>() {
        override fun areItemsTheSame(
            oldItem: TransactionDetailsViewItem,
            newItem: TransactionDetailsViewItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionDetailsViewItem,
            newItem: TransactionDetailsViewItem
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun accept(newItems: List<TransactionDetailsViewItem>, commitCallback: Runnable? = null) {
        differ.submitList(newItems, commitCallback)
    }

    override fun getItemViewType(position: Int) =
        differ.currentList[position].type.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (val itemType = TransactionDetailsViewItemType.values()[viewType]) {
            TransactionDetailsViewItemType.HEADER -> {
                val bindings: DetailsHeaderViewItemBinding =
                    DataBindingUtil.inflate(
                        inflater, itemType.layoutId, parent, false
                    )
                TransactionDetailsViewHolder.HeaderViewHolder(bindings)
            }
            TransactionDetailsViewItemType.ATTRIBUTE -> {
                val bindings: DetailsAttributeViewItemBinding =
                    DataBindingUtil.inflate(
                        inflater, itemType.layoutId, parent, false
                    )
                TransactionDetailsViewHolder.AttributeViewHolder(bindings)
            }
        }
    }

    override fun onBindViewHolder(holder: TransactionDetailsViewHolder, position: Int) {
        val item = differ.currentList[position]
        when (holder) {
            is TransactionDetailsViewHolder.HeaderViewHolder -> holder.bind(
                item as TransactionDetailsViewItem.Header,
            )
            is TransactionDetailsViewHolder.AttributeViewHolder -> holder.bind(
                item as TransactionDetailsViewItem.Attribute
            )
        }
    }

    override fun getItemCount() = differ.currentList.size
}