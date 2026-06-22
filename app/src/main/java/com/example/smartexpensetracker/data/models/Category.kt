package com.example.smartexpensetracker.data.models

data class Category(
    val id: String = "",
    val name: String = "",
    val type: String = "expense" // "expense" or "income"
)
