package com.example.financeapp.data.repository

import com.example.financeapp.data.local.datastore.PreferencesManager
import com.example.financeapp.domain.model.DataSourceType
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

class DynamicTransactionRepository(
    private val preferencesManager: PreferencesManager,
    private val roomImpl: TransactionRepositoryRoomImpl,
    private val remoteImpl: TransactionRepositoryRemoteImpl,
    private val firebaseImpl: TransactionRepositoryFirebaseImpl
) : TransactionRepository {

    private suspend fun getActiveRepository(): TransactionRepository {
        return when (preferencesManager.selectedDataSource.first()) {
            DataSourceType.ROOM -> roomImpl
            DataSourceType.API -> remoteImpl
            DataSourceType.FIREBASE -> firebaseImpl
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTransactions(): Flow<List<Transaction>> {
        return preferencesManager.selectedDataSource.flatMapLatest { source ->
            when (source) {
                DataSourceType.ROOM -> roomImpl.getTransactions()
                DataSourceType.API -> remoteImpl.getTransactions()
                DataSourceType.FIREBASE -> firebaseImpl.getTransactions()
            }
        }
    }

    override suspend fun insertTransaction(transactions: Transaction) {
        getActiveRepository().insertTransaction(transactions)
    }

    override suspend fun deleteTransacrion(id: String) {
        getActiveRepository().deleteTransacrion(id)
    }

    override suspend fun updateTransacrion(transactions: Transaction) {
        getActiveRepository().updateTransacrion(transactions)
    }
}