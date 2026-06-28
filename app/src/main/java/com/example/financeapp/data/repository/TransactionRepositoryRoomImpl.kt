package com.example.financeapp.data.repository

import com.example.financeapp.data.local.room.dao.TransactionDao
import com.example.financeapp.data.local.room.entity.TransactionEntity
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDateTime

class TransactionRepositoryRoomImpl(private val dao: TransactionDao) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return dao.getTransactions().map { entities ->
            entities.map { entity ->
                Transaction(
                    id = entity.id,
                    description = entity.description,
                    amount = BigDecimal(entity.amount),
                    date = LocalDateTime.parse(entity.date),
                    type = TransactionType.valueOf(entity.type)
                )
            }
        }
    }

    override suspend fun insertTransaction(transactions: Transaction) {
        dao.insertTransaction(transactions.toEntity())
    }

    override suspend fun deleteTransacrion(id: String) {
        dao.deleteTransaction(id)
    }

    override suspend fun updateTransacrion(transactions: Transaction) {
        dao.updateTransaction(transactions.toEntity())
    }

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        description = description,
        amount = amount.toString(),
        date = date.toString(),
        type = type.name
    )
}