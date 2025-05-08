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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference.child("Transactions")

    // Function to add a transaction to the database
    fun addTransaction(
        context: Context,
        amount: String,
        category: String,
        type: String, // "income" or "expense"
        date: String,
        title: String, // Transaction title
        description: String, // Transaction description
        userId: String, // User ID
        navController: NavController,
        name: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transactionId = database.push().key ?: ""
                val transaction = TransactionModel(
                    amount = amount,
                    category = category,
                    transactionId = transactionId,
                    description = description,
                    transactionType = type,
                    title = title,
                    userId = userId,
                    date = date
                )
                database.child(transactionId).setValue(transaction)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                            }
                        }
                    }
                    .addOnFailureListener {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to add transaction", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Function to view all transactions
    fun viewTransactions(
        transaction: MutableState<TransactionModel>,
        transactions: SnapshotStateList<TransactionModel>,
        context: Context
    ): SnapshotStateList<TransactionModel> {
        val ref = FirebaseDatabase.getInstance().getReference("Transactions")
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

    // Function to update a transaction
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
        val databaseReference = FirebaseDatabase.getInstance().getReference("Transactions/$transactionId")
        val updatedTransaction = TransactionModel(
            amount = amount.toString(),
            category = category,
            transactionId = transactionId,
            description = description,
            transactionType = type,
            title = title,
            userId = userId,
            date = date
        )
        databaseReference.setValue(updatedTransaction)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Transaction updated successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_VIEW_TRANSACTIONS)
                } else {
                    Toast.makeText(context, "Failed to update transaction", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Function to delete a transaction
    fun deleteTransaction(
        context: Context,
        transactionId: String,
        navController: NavController
    ) {
        AlertDialog.Builder(context)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Yes") { _, _ ->
                val databaseReference = FirebaseDatabase.getInstance().getReference("Transactions/$transactionId")
                databaseReference.removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to delete transaction", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

