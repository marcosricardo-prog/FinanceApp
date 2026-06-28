package com.example.financeapp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val description: String,
    val amount: String, // Salvo como string para preservar a precisão do BigDecimal
    val date: String,    // Salvo como string formato ISO (LocalDateTime)
    val type: String
)