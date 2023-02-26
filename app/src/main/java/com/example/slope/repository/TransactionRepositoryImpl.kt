package com.example.slope.repository

import android.content.Context
import com.example.models.Transaction
import com.example.networking.json.JSONObjectAdapter
import com.example.networking.json.LocalDateAdapter
import com.example.networking.json.LocalDateTimeAdapter
import com.example.networking.util.ApiResult
import com.example.networking.webservices.TransactionsWebservice
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.Type

class TransactionRepositoryImpl(
    private val transactionsWebservice: TransactionsWebservice,
    retrofit: Retrofit,
    @ApplicationContext private val context: Context
) : BaseRepository(retrofit), TransactionRepository {

    private val _transactionsFlow: MutableStateFlow<ApiResult<List<Transaction>>?> =
        MutableStateFlow(null)
    override val transactionsFlow: StateFlow<ApiResult<List<Transaction>>?> = _transactionsFlow

    override suspend fun getTransactions(): Flow<ApiResult<List<Transaction>>?> {
        return flow {
            emit(ApiResult.loading())

            // use this block if I was actually making a network call
//            val result = getResponse(
//                request = {
//                    transactionsWebservice.getTransactions()
//                },
//                defaultErrorMessage = "Error fetching transactions"
//            )

            // using this to mock the response from a local json file
            try {
                val jsonString = context.assets.open("transactions.json")
                    .bufferedReader()
                    .use { it.readText() }
                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .add(LocalDateTimeAdapter())
                    .add(LocalDateAdapter())
                    .build()
                val type: Type =
                    Types.newParameterizedType(List::class.java, Transaction::class.java)
                val adapter: JsonAdapter<List<Transaction>> = moshi.adapter(type)
                val response = adapter.fromJson(jsonString) ?: emptyList()
                _transactionsFlow.value = ApiResult.success(response)
                emit(ApiResult.success(response))
            } catch (e: Exception) {
                Timber.e(e, "Failed to read local json: ${e.message}")
            }
        }.flowOn(Dispatchers.IO)
    }
}