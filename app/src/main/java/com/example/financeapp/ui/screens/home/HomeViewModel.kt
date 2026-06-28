package com.example.financeapp.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.datastore.PreferencesManager
import com.example.financeapp.domain.model.DataSourceType
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.domain.usecase.GetTransactionsUseCase
import com.example.financeapp.domain.usecase.InsertTransactionsUseCase
import com.example.financeapp.domain.usecase.UpdateTransactionUseCase
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class HomeViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val insertTransactionsUseCase: InsertTransactionsUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var state by mutableStateOf(HomeUiState())
        private set

    init {
        // Coleta transações com loading state
        viewModelScope.launch {
            try {
                getTransactionsUseCase().collect { data ->
                    state = state.copy(transactions = data, isLoading = false, errorMessage = null)
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar transações: ${e.message}"
                )
            }
        }

        // Coleta a fonte de dados ativa
        viewModelScope.launch {
            preferencesManager.selectedDataSource.collect { source ->
                state = state.copy(currentDataSource = source, isLoading = true)
            }
        }
    }

    fun changeDataSource(type: DataSourceType) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            preferencesManager.saveDataSource(type)
        }
    }

    fun addTransaction(
        description: String,
        amount: BigDecimal,
        date: LocalDateTime,
        type: Boolean
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                description = description,
                amount = amount,
                date = date,
                type = if (type) TransactionType.INCOME else TransactionType.EXPENSE
            )
            insertTransactionsUseCase(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch { updateTransactionUseCase(transaction) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { deleteTransactionUseCase(transaction.id) }
    }
}
