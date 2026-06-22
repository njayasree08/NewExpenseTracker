package com.example.smartexpensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val type: String, // "expense" or "income"
    val categoryId: String,
    val date: Long,
    val description: String
)
