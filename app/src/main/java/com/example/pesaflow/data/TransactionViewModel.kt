package com.example.pesaflow.data

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pesaflow.models.TransactionModel
import com.example.pesaflow.navigations.ROUTE_VIEW_TRANSACTIONS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class TransactionViewModel : ViewModel() {

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Initialize Firebase Database properly with error handling
    private var database: DatabaseReference? = null

    init {
        try {
            database = FirebaseDatabase.getInstance().reference.child("Transactions")
        } catch (e: Exception) {
            // Log initialization error
            println("Firebase initialization error: ${e.localizedMessage}")
        }
    }

    // Get current user ID or null if not logged in
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // ✅ FIXED ADD TRANSACTION
    fun addTransaction(
        context: Context,
        amount: String,
        category: String,
        type: String,
        date: String,
        title: String,
        description: String,
        navController: NavController,
        userIdParam: String  // Renamed parameter to avoid conflict
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Use parameterized userId if we have it, otherwise get from auth
                val userId = if (userIdParam.isNotEmpty()) userIdParam else getCurrentUserId()

                if (userId == null || userId.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Validate database connection
                if (database == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Database connection failed", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Validate amount input
                val amountInt = try {
                    amount.toDouble().toInt()  // Convert to double first to handle decimal inputs
                } catch (e: NumberFormatException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invalid amount format: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Generate unique transaction ID
                val transactionRef = database!!.child(userId).push()
                val transactionId = transactionRef.key

                if (transactionId == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to generate transaction ID", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val transaction = TransactionModel(
                    amount = amountInt,
                    category = category,
                    transactionId = transactionId,
                    description = description,
                    transactionType = type,
                    title = title,
                    userId = userId,
                    date = date
                )

                // Save transaction to Firebase with better error handling
                try {
                    transactionRef.setValue(transaction)
                        .addOnSuccessListener {
                            viewModelScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to add: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Firebase error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ✅ VIEW TRANSACTIONS
    fun viewTransactions(
        transaction: MutableState<TransactionModel>,
        transactions: SnapshotStateList<TransactionModel>,
        context: Context
    ): SnapshotStateList<TransactionModel> {
        try {
            // Get current user ID
            val userId = getCurrentUserId()
            if (userId == null) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                return transactions
            }

            // Validate database connection
            if (database == null) {
                Toast.makeText(context, "Database connection failed", Toast.LENGTH_SHORT).show()
                return transactions
            }

            // Use consistent path structure
            val ref = database!!.child(userId)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    transactions.clear()
                    for (snap in snapshot.children) {
                        val value = snap.getValue(TransactionModel::class.java)
                        value?.let { transactions.add(it) }
                    }
                    if (transactions.isNotEmpty()) transaction.value = transactions.first()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch transactions: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
        return transactions
    }

    // ✅ GET TRANSACTION BY ID
    fun getTransactionById(
        transactionId: String,
        context: Context,
        onSuccess: (TransactionModel) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            // Get current user ID
            val userId = getCurrentUserId()
            if (userId == null) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                onFailure()
                return
            }

            // Validate database connection
            if (database == null) {
                Toast.makeText(context, "Database connection failed", Toast.LENGTH_SHORT).show()
                onFailure()
                return
            }

            // Query the transaction by ID
            database!!.child(userId).child(transactionId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transaction = snapshot.getValue(TransactionModel::class.java)
                    if (transaction != null) {
                        onSuccess(transaction)
                    } else {
                        Toast.makeText(context, "Transaction not found", Toast.LENGTH_SHORT).show()
                        onFailure()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to get transaction: ${error.message}", Toast.LENGTH_SHORT).show()
                    onFailure()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            onFailure()
        }
    }

    // ✅ UPDATE TRANSACTION - FIXED
    fun updateTransaction(
        context: Context,
        navController: NavController,
        amount: Double,
        category: String,
        type: String,
        title: String,
        description: String,
        transactionId: String,
        date: String,
        userIdParam: String  // Renamed parameter to avoid conflict
    ) {
        try {
            // Use parameterized userId if we have it, otherwise get from auth
            val userId = if (userIdParam.isNotEmpty()) userIdParam else getCurrentUserId()

            if (userId == null || userId.isEmpty()) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate database connection
            if (database == null) {
                Toast.makeText(context, "Database connection failed", Toast.LENGTH_SHORT).show()
                return
            }

            val updatedTransaction = TransactionModel(
                amount = amount.toInt(),
                category = category,
                transactionId = transactionId,
                description = description,
                transactionType = type,
                title = title,
                userId = userId,
                date = date
            )

            // Use consistent path structure with better error handling
            database!!.child(userId).child(transactionId).setValue(updatedTransaction)
                .addOnSuccessListener {
                    Toast.makeText(context, "Transaction updated successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to update: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ DELETE TRANSACTION
    fun deleteTransaction(
        context: Context,
        transactionId: String,
        navController: NavController
    ) {
        try {
            // Get current user ID
            val userId = getCurrentUserId()
            if (userId == null) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate database connection
            if (database == null) {
                Toast.makeText(context, "Database connection failed", Toast.LENGTH_SHORT).show()
                return
            }

            // Confirm deletion via AlertDialog
            AlertDialog.Builder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes") { _, _ ->
                    // Use consistent path structure
                    database!!.child(userId).child(transactionId).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Failed to delete: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}