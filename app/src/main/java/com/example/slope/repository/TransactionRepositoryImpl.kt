package com.example.slope.repository

import com.example.models.Transaction
import com.example.networking.util.ApiResult
import com.example.networking.webservices.TransactionsWebservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Retrofit

class TransactionRepositoryImpl(
    private val transactionsWebservice: TransactionsWebservice,
    retrofit: Retrofit,
) : BaseRepository(retrofit), TransactionRepository {

    private val _transactionsFlow: MutableStateFlow<ApiResult<List<Transaction>>?> =
        MutableStateFlow(null)
    override val transactionsFlow: StateFlow<ApiResult<List<Transaction>>?> = _transactionsFlow

    override suspend fun getTransactions(): Flow<ApiResult<List<Transaction>>?> {
        return flow {
            emit(ApiResult.loading())
            val result = getResponse(
                request = {
                    transactionsWebservice.getTransactions()
                },
                defaultErrorMessage = "Error fetching transactions"
            )

            _transactionsFlow.value = ApiResult(
                result.status,
                result.data,
                result.error,
                result.message ?: result.error?.statusMessage ?: "Unable to fetch transactions"
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}