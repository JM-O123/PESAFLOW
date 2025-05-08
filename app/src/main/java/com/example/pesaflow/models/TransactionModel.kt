package com.example.pesaflow.models

data class TransactionModel(
    var amount: String = 0.0,  // Amount of the transaction (numeric, for calculations)
    var description: String = "",  // Description or notes about the transaction
    var transactionType: String = "",  // Type: "income" or "expense"
    var transactionId: String = "",  // Unique identifier
    val title: String = "",
    val date: String = "",
    val category: String = "",
    val userId: String = ""
)

data class UserModel(
    var userId: String = "",
    var userName: String = "",
    var email: String = "",
    var balance: Double = 0.0,  // Calculated from transactions
    val firstname: String = ""
)
