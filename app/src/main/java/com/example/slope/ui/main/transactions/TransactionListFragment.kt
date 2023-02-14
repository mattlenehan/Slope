package com.example.slope.ui.main.transactions

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networking.util.ApiResult
import com.example.slope.R
import com.example.slope.databinding.DateHeaderViewItemBinding
import com.example.slope.databinding.EmptyViewItemBinding
import com.example.slope.databinding.FragmentTransactionListBinding
import com.example.slope.databinding.SortViewItemBinding
import com.example.slope.databinding.TransactionViewItemBinding
import com.example.slope.ui.main.MainViewModel
import com.example.slope.ui.main.SortOption
import com.example.slope.ui.main.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TransactionListFragment : Fragment() {

    private val viewModel: MainViewModel
            by navGraphViewModels(R.id.nav_graph) {
                defaultViewModelProviderFactory
            }

    private val transactionsAdapter by lazy {
        ListAdapter(listener = adapterListener)
    }

    private val searchResultsAdapter by lazy {
        ListAdapter(listener = adapterListener)
    }

    private var _binding: FragmentTransactionListBinding? = null
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
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        binding.recycler.apply {
            adapter = transactionsAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }

        binding.searchResultsRecycler.apply {
            adapter = searchResultsAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchBar.text = binding.searchView.text
            binding.searchView.hide()
            false
        }
        binding.searchView.editText.addTextChangedListener {
            viewModel.onSearchTextChanged(text = it.toString(), debounce = false)
        }
    }

    private fun subscribeUi() {
        viewModel.transactions.observe(
            viewLifecycleOwner
        ) {
            when (it.status) {
                ApiResult.Status.SUCCESS -> {
                    binding.recycler.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                    transactionsAdapter.accept(it.data ?: emptyList()) {
                        binding.recycler.scrollToPosition(0)
                    }
                }
                ApiResult.Status.ERROR -> {
                    binding.recycler.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                    view?.showSnackbar(it.error?.statusMessage ?: it.message)
                }
                ApiResult.Status.LOADING -> {
                    binding.recycler.visibility = View.GONE
                    binding.loading.visibility = View.VISIBLE
                }
            }
        }

        viewModel.searchTextFlow.asLiveData().observe(
            viewLifecycleOwner
        ) {
            binding.searchBar.text =
                it ?: binding.root.context.getString(R.string.search_transactions)
        }

        viewModel.searchResults.observe(
            viewLifecycleOwner
        ) {
            binding.searchResultsRecycler.visibility = View.VISIBLE
            binding.loading.visibility = View.GONE
            searchResultsAdapter.accept(it ?: emptyList()) {
                binding.searchResultsRecycler.scrollToPosition(0)
            }
        }
    }

    private val adapterListener = object : AdapterListener {
        override fun onTransactionClick(id: Int, merchant: String) {
            val direction = TransactionListFragmentDirections.openTransactionDetails(id, merchant)
            findNavController().navigate(direction)
        }

        override fun onSortOptionSelected(sortOption: SortOption) {
            viewModel.onSortOptionSelected(sortOption)
        }
    }
}

private interface AdapterListener {
    fun onTransactionClick(id: Int, merchant: String)
    fun onSortOptionSelected(sortOption: SortOption)
}

private class ListAdapter(private val listener: AdapterListener) :
    RecyclerView.Adapter<TransactionViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<TransactionViewItem>() {
        override fun areItemsTheSame(
            oldItem: TransactionViewItem,
            newItem: TransactionViewItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionViewItem,
            newItem: TransactionViewItem
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun accept(newItems: List<TransactionViewItem>, commitCallback: Runnable? = null) {
        differ.submitList(newItems, commitCallback)
    }

    override fun getItemViewType(position: Int) =
        differ.currentList[position].type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (val itemType = TransactionViewItemType.values()[viewType]) {
            TransactionViewItemType.TRANSACTION_LIST_ITEM -> {
                val bindings: TransactionViewItemBinding =
                    DataBindingUtil.inflate(
                        inflater, itemType.layoutId, parent, false
                    )
                TransactionViewHolder.TransactionListViewHolder(bindings)
            }
            TransactionViewItemType.DATE_HEADER -> {
                val bindings: DateHeaderViewItemBinding =
                    DataBindingUtil.inflate(
                        inflater, itemType.layoutId, parent, false
                    )
                TransactionViewHolder.DateHeaderViewHolder(bindings)
            }
            TransactionViewItemType.SORT_ITEM -> {
                val bindings: SortViewItemBinding =
                    DataBindingUtil.inflate(
                        inflater, itemType.layoutId, parent, false
                    )
                TransactionViewHolder.SortViewHolder(bindings)
            }
            TransactionViewItemType.EMPTY -> {
                val bindings: EmptyViewItemBinding =
                    DataBindingUtil.inflate(
                        inflater, itemType.layoutId, parent, false
                    )
                TransactionViewHolder.EmptyViewHolder(bindings)
            }
        }
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = differ.currentList[position]
        when (holder) {
            is TransactionViewHolder.TransactionListViewHolder -> holder.bind(
                item as TransactionViewItem.TransactionListItem,
                onClick = { id, name -> listener.onTransactionClick(id, name) }
            )
            is TransactionViewHolder.DateHeaderViewHolder -> holder.bind(
                item as TransactionViewItem.DateHeader
            )
            is TransactionViewHolder.SortViewHolder -> holder.bind(
                item as TransactionViewItem.SortItem,
                onNewSortOptionSelected = { sortOption -> listener.onSortOptionSelected(sortOption) }
            )
            is TransactionViewHolder.EmptyViewHolder -> {}
        }
    }

    override fun getItemCount() = differ.currentList.size
}