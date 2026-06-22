package com.example.smartexpensetracker.data.models

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val type: String = "expense", // "expense" or "income"
    val categoryId: String = "",
    val date: Long = System.currentTimeMillis(),
    val description: String = ""
)
