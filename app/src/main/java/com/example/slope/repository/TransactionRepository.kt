package com.example.slope.repository

import com.example.models.Transaction
import com.example.networking.util.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TransactionRepository {

    val transactionsFlow: StateFlow<ApiResult<List<Transaction>>?>

    suspend fun getTransactions(): Flow<ApiResult<List<Transaction>>?>
}