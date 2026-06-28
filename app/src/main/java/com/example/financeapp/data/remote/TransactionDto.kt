package com.example.financeapp.data.remote

data class TransactionDto(
    val id: String? = null,
    val description: String,
    val amount: Double,
    val date: String,
    val type: String
)

