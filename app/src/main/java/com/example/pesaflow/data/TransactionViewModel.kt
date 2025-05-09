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
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference.child("Transactions")

    // ✅ ADD TRANSACTION
    fun addTransaction(
        context: Context,
        amount: String,
        category: String,
        type: String,
        date: String,
        title: String,
        description: String,
        userId: String,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Validate amount input
                val amountInt = try {
                    amount.toInt()
                } catch (e: NumberFormatException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Generate unique transaction ID
                val transactionRef = database.child(userId).child("Transactions").push()
                val transactionId = transactionRef.key ?: return@launch

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

                // Save transaction to Firebase
                transactionRef.setValue(transaction).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                            }
                        }
                    } else {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to add transaction: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
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
        val ref = database
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
        return transactions
    }

    // ✅ UPDATE TRANSACTION
    fun updateTransaction(
        context: Context,
        navController: NavController,
        amount: Double,
        category: String,
        type: String,
        title: String,
        description: String,
        transactionId: String,
        userId: String,
        date: String
    ) {
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

        // Update transaction in Firebase
        database.child(transactionId).setValue(updatedTransaction)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Transaction updated successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                } else {
                    Toast.makeText(context, "Failed to update transaction", Toast.LENGTH_LONG).show()
                }
            }
    }

    // ✅ DELETE TRANSACTION
    fun deleteTransaction(
        context: Context,
        transactionId: String,
        navController: NavController
    ) {
        // Confirm deletion via AlertDialog
        AlertDialog.Builder(context)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Yes") { _, _ ->
                // Remove the transaction from Firebase
                database.child(transactionId).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                        } else {
                            Toast.makeText(context, "Failed to delete transaction", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

