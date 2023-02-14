package com.example.networking.webservices

import com.example.models.Transaction
import retrofit2.Response
import retrofit2.http.GET

interface TransactionsWebservice {
    @GET("/transactions.json/")
    suspend fun getTransactions(): Response<List<Transaction>>
}
