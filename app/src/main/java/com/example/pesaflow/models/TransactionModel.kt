package com.example.pesaflow.models

data class TransactionModel(
    val amount: Int = 0,
    val category: String = "",
    val transactionId: String = "",
    val description: String = "",
    val transactionType: String = "",
    val title: String = "",
    val userId: String = "",
    val date: String = ""
)