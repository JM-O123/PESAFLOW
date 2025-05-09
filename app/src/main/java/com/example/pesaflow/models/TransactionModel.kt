package com.example.pesaflow.models

data class TransactionModel(
    var transactionId: String = "",       // Unique ID
    var userId: String = "",              // Owner's UID
    var title: String = "",               // Short title of transaction
    var description: String = "",         // Additional details
    var category: String = "",            // e.g., Food, Bills, Salary
    var transactionType: String = "",     // "income" or "expense"
    var amount: Int = 0,             // Double for currency precision
    var date: String = "",                // Display date (e.g., "2025-05-08")
    var createdAt: Long = 0L              // Timestamp for sorting (Epoch millis)
)


data class User(
    var userId: String = "",
    var userName: String = "",
    var email: String = "",
    var balance: Double = 0.0,  // Calculated from transactions
    val firstname: String = ""
)
