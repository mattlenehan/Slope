package com.example.slope.di

import android.content.Context
import com.example.networking.webservices.TransactionsWebservice
import com.example.slope.repository.TransactionRepository
import com.example.slope.repository.TransactionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTransactionRepository(
        transactionsWebservice: TransactionsWebservice,
        retrofit: Retrofit,
        @ApplicationContext appContext: Context,
    ): TransactionRepository {
        return TransactionRepositoryImpl(
            transactionsWebservice = transactionsWebservice,
            retrofit = retrofit,
            context = appContext
        )
    }
}