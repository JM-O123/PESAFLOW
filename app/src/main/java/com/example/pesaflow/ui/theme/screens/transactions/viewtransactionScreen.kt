package com.example.pesaflow.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pesaflow.data.TransactionViewModel
import com.example.pesaflow.models.TransactionModel

@Composable
fun ViewTransactionsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val transactionViewModel = remember { TransactionViewModel() }

    val emptyTransaction = remember {
        mutableStateOf(
            TransactionModel(
                amount = 0.toInt(),
                description = "",
                transactionType = "",
                transactionId = "",
                title = "",
                date = "",
                category = "",
                userId = ""
            )
        )
    }

    val transactionList = remember { mutableStateListOf<TransactionModel>() }

    // Load transactions once when Composable is shown
    LaunchedEffect(Unit) {
        transactionViewModel.viewTransactions(emptyTransaction, transactionList, context)
    }

    // Function to delete a transaction
    val deleteTransaction = { transactionId: String ->
        transactionViewModel.deleteTransaction(context, transactionId, navController)
        // After deletion, we refresh the list by re-fetching transactions
        transactionList.clear()
        transactionViewModel.viewTransactions(emptyTransaction, transactionList, context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "All Transactions",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(transactionList) { txn ->
                // Pass the deleteTransaction logic to each item
                TransactionItem(
                    transaction = txn,
                    onDeleteClick = deleteTransaction
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionModel,
    onDeleteClick: (String) -> SnapshotStateList<TransactionModel> // This function will be triggered on delete
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(text = "Title: ${transaction.title}", fontWeight = FontWeight.Bold)
        Text(text = "Amount: ${transaction.amount}")
        Text(text = "Category: ${transaction.category}")
        Text(text = "Type: ${transaction.transactionType}")
        Text(text = "Date: ${transaction.date}")
        Spacer(modifier = Modifier.height(8.dp))

        // Add Delete Button
        Button(
            onClick = { onDeleteClick(transaction.transactionId) },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
        ) {
            Text(text = "Delete", color = Color.White)
        }
    }
}
