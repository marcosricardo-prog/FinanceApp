package com.example.financeapp.data.repository

import com.example.financeapp.data.remote.DeleteRequest
import com.example.financeapp.data.remote.TransactionApi
import com.example.financeapp.data.remote.TransactionDto
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class TransactionRepositoryRemoteImpl(private val api: TransactionApi) : TransactionRepository {

    private val refreshTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun getTransactions(): Flow<List<Transaction>> = flow {
        refreshTrigger.collect {
            try {
                val list = api.getTransactions().map { dto ->
                    Transaction(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        description = dto.description,
                        amount = BigDecimal.valueOf(dto.amount),
                        date = LocalDateTime.parse(dto.date),
                        type = if (dto.type.lowercase() == "income" || dto.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
                    )
                }
                emit(list)
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    override suspend fun insertTransaction(transactions: Transaction) {
        val dto = TransactionDto(
            description = transactions.description,
            amount = transactions.amount.toDouble(),
            date = transactions.date.toString(),
            type = transactions.type.name
        )
        api.insertTransaction(dto)
        triggerRefresh()
    }

    override suspend fun deleteTransacrion(id: String) {
        // MockAPI deleta via DELETE na rota /transactions/{id}
        api.deleteTransaction(id)
        triggerRefresh()
    }

    override suspend fun updateTransacrion(transactions: Transaction) {
        val dto = TransactionDto(
            id = transactions.id,
            description = transactions.description,
            amount = transactions.amount.toDouble(),
            date = transactions.date.toString(),
            type = transactions.type.name
        )

        // MockAPI atualiza via PUT na rota /transactions/{id}
        api.updateTransaction(transactions.id, dto)
        triggerRefresh()
    }

    private fun triggerRefresh() {
        refreshTrigger.value = System.currentTimeMillis()
    }
}