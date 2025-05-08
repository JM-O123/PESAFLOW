package com.example.pesaflow.models

data class User(
    var userId: String = "",       // Unique identifier for the user
    var userName: String = "",     // Display name of the user
    var email: String = "",        // User's email, used for authentication
    var password: String = "",     // User's password (should be hashed/stored securely)
    var balance: Double = 0.0,     // User's current balance (total income - total expenses)
    var transactions: List<String> = listOf()  // List of transaction IDs for the user
)
