package com.example.pesaflow.ui.theme.screens.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import com.example.pesaflow.navigations.ROUTE_HOME
import com.example.pesaflow.navigations.ROUTE_LOGIN
import com.example.pesaflow.navigations.ROUTE_UPDATE_TRANSACTION

@Composable
fun ViewTransactionsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val transactionViewModel = remember { TransactionViewModel() }

    // Get the current user ID from the transaction view model
    val userId = transactionViewModel.getCurrentUserId()

    // Check if user is logged in
    LaunchedEffect(Unit) {
        if (userId.isNullOrEmpty()) {
            // Redirect to login if not logged in
            Toast.makeText(context, "Please login to view transactions", Toast.LENGTH_SHORT).show()
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }
    }

    // Show loading indicator while checking authentication
    if (userId.isNullOrEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val emptyTransaction = remember {
        mutableStateOf(
            TransactionModel(
                amount = 0,
                description = "",
                transactionType = "",
                transactionId = "",
                title = "",
                date = "",
                category = "",
                userId = userId
            )
        )
    }

    val transactionList = remember { mutableStateListOf<TransactionModel>() }

    // Load transactions once when Composable is shown
    LaunchedEffect(userId) {
        transactionViewModel.viewTransactions(emptyTransaction, transactionList, context)
    }

    // Function to delete a transaction
    val deleteTransaction = { transactionId: String ->
        transactionViewModel.deleteTransaction(
            context = context,
            transactionId = transactionId,
            navController = navController
        )
        // The refresh will happen automatically through the ValueEventListener
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back to Home Button
        Button(
            onClick = { navController.navigate(ROUTE_HOME) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text(text = "Back to Home")
        }

        Text(
            text = "All Transactions",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (transactionList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions found",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactionList) { txn ->
                    // Pass the deleteTransaction and navigation logic to each item
                    TransactionItem(
                        transaction = txn,
                        onDeleteClick = { id ->
                            deleteTransaction(id)
                            transactionList
                        },
                        onUpdateClick = { id ->
                            navController.navigate("$ROUTE_UPDATE_TRANSACTION/$id")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionModel,
    onDeleteClick: (String) -> SnapshotStateList<TransactionModel>, // This function will be triggered on delete
    onUpdateClick: (String) -> Unit // This function will be triggered on update
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Delete Button
                Button(
                    onClick = { onDeleteClick(transaction.transactionId) },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Delete", color = Color.White)
                }

                // Update Button
                Button(
                    onClick = { onUpdateClick(transaction.transactionId) },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Update", color = Color.White)
                }
            }
        }
    }
}