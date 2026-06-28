package com.example.financeapp.ui.screens.home

import com.example.financeapp.domain.model.DataSourceType
import com.example.financeapp.domain.model.Transaction

data class HomeUiState(
    val transactions: List<Transaction> = emptyList(),
    val currentDataSource: DataSourceType = DataSourceType.FIREBASE,
    val isLoading: Boolean = true,   // Loading state para feedback visual
    val errorMessage: String? = null
)
