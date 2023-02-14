package com.example.slope.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networking.util.ApiResult
import com.example.slope.R
import com.example.slope.repository.TransactionRepository
import com.example.slope.ui.main.details.TransactionDetailsViewItem
import com.example.slope.ui.main.transactions.TransactionViewItem
import com.example.slope.ui.main.util.splitCamelCase
import com.example.slope.ui.main.util.toMap
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _transactions = MutableLiveData<ApiResult<List<TransactionViewItem>?>>()
    val transactions: LiveData<ApiResult<List<TransactionViewItem>?>> = _transactions

    private val _searchResults = MutableLiveData<List<TransactionViewItem>?>()
    val searchResults: LiveData<List<TransactionViewItem>?> = _searchResults

    private val _details = MutableLiveData<List<TransactionDetailsViewItem>?>()
    val details: LiveData<List<TransactionDetailsViewItem>?> = _details

    private val sortOptionFlow = MutableStateFlow(SortOption.DATE_NEWEST)

    val searchTextFlow = MutableStateFlow("")

    private var searchTextChangedJob: Job? = null

    private val excludedFields = setOf(
        "id",
        "amount",
        "date",
        "logoUrl"
    )

    init {
        viewModelScope.launch {
            transactionRepository.getTransactions().collect()
            combine(
                transactionRepository.transactionsFlow,
                sortOptionFlow,
                searchTextFlow
            ) { transactionsResult, sortOption, query ->
                Triple(transactionsResult, sortOption, query)
            }.collect { (transactionsResult, sortOption, query) ->
                when (transactionsResult?.status) {
                    ApiResult.Status.SUCCESS -> {
                        val regex = Regex(".*$query.*")
                        val viewItems = mutableListOf<TransactionViewItem>()
                        var targetItemDate: LocalDate? = null
                        val transactions = when (sortOption) {
                            SortOption.DATE_NEWEST -> {
                                transactionsResult.data?.reversed()
                            }
                            SortOption.DATE_OLDEST -> {
                                transactionsResult.data
                            }
                            SortOption.MERCHANT -> {
                                transactionsResult.data?.sortedBy { it.merchantName }
                            }
                            SortOption.AMOUNT -> {
                                transactionsResult.data?.sortedBy { it.amount }?.reversed()
                            }
                        } ?: emptyList()
                        transactions.filter {
                            it.merchantName.lowercase().matches(regex) ||
                                    it.merchantCategory.lowercase().matches(regex)
                        }.forEach {
                            // get display date
                            val itemDate = it.date.toLocalDate()
                            // check if this is a new date
                            if (itemDate.equals(targetItemDate)) {
                                viewItems.add(
                                    TransactionViewItem.TransactionListItem(
                                        id = it.id.toString(),
                                        transaction = it
                                    )
                                )
                            } else {
                                if (sortOption == SortOption.DATE_OLDEST ||
                                    sortOption == SortOption.DATE_NEWEST
                                ) {
                                    targetItemDate = itemDate
                                    viewItems.add(
                                        TransactionViewItem.DateHeader(
                                            id = itemDate.toEpochDay().toString(),
                                            localDate = itemDate
                                        )
                                    )
                                }
                                viewItems.add(
                                    TransactionViewItem.TransactionListItem(
                                        id = it.id.toString(),
                                        transaction = it
                                    )
                                )
                            }
                        }
                        if (viewItems.isEmpty()) {
                            viewItems.add(TransactionViewItem.EmptyState())
                        } else {
                            viewItems.add(
                                0,
                                TransactionViewItem.SortItem(
                                    id = sortOption.ordinal.toString(),
                                    selectedSortOption = sortOption
                                )
                            )
                        }
                        _transactions.value = ApiResult.success(viewItems)

                        val searchViewItems = mutableListOf<TransactionViewItem>()
                        transactionsResult.data?.reversed()?.filter {
                            it.merchantName.lowercase().matches(regex) ||
                                    it.merchantCategory.lowercase().matches(regex)
                        }?.forEach {
                            searchViewItems.add(
                                TransactionViewItem.TransactionListItem(
                                    it.id.toString(),
                                    it
                                )
                            )
                        }
                        _searchResults.value = searchViewItems
                    }
                    ApiResult.Status.ERROR -> {
                        _transactions.value = ApiResult.error(
                            transactionsResult.message ?: "Unable to fetch transactions",
                            transactionsResult.error
                        )
                    }
                    ApiResult.Status.LOADING -> {
                        _transactions.value = ApiResult.loading()
                    }
                    null -> {}
                }
            }
        }
    }

    fun getTransactionDetails(id: Int) {
        viewModelScope.launch {
            transactionRepository.transactionsFlow.collect { response ->
                if (response?.status == ApiResult.Status.SUCCESS) {
                    val viewItems = mutableListOf<TransactionDetailsViewItem>()
                    val transaction = response.data?.firstOrNull {
                        it.id == id
                    }
                    val jsonString = Gson().toJson(transaction)
                    val jsonObj = JSONObject(jsonString)
                    val attributeMap = jsonObj.toMap()

                    transaction?.let {
                        viewItems.add(
                            TransactionDetailsViewItem.Header(
                                id = transaction.id.toString(),
                                transaction = transaction
                            )
                        )
                        attributeMap.entries.forEach { entry ->
                            if (entry.value != null &&
                                entry.value.toString().isNotEmpty() &&
                                !excludedFields.contains(entry.key)
                            ) {
                                viewItems.add(
                                    TransactionDetailsViewItem.Attribute(
                                        id = entry.key.hashCode().toString(),
                                        key = entry.key.splitCamelCase(),
                                        value = entry.value.toString()
                                    )
                                )
                            }
                        }
                    }
                    _details.value = viewItems
                }
            }
        }
    }

    fun onSearchTextChanged(text: String = "", debounce: Boolean = true) {
        searchTextChangedJob?.cancel()
        // Debounce input by waiting until the user finishes typing before issuing the network
        // request.
        searchTextChangedJob = viewModelScope.launch {
            if (debounce) {
                delay(1000) // 1 second
            }
            searchTextFlow.value = text
        }
    }

    fun onSortOptionSelected(sortOption: SortOption) {
        sortOptionFlow.value = sortOption
    }
}

enum class SortOption {
    DATE_NEWEST,
    DATE_OLDEST,
    MERCHANT,
    AMOUNT
}

fun SortOption.getDisplayName(context: Context): String {
    return when (this) {
        SortOption.DATE_NEWEST -> context.getString(R.string.newest_first)
        SortOption.DATE_OLDEST -> context.getString(R.string.oldest_first)
        SortOption.MERCHANT -> context.getString(R.string.merchant_a_z)
        SortOption.AMOUNT -> context.getString(R.string.amount)
    }
}