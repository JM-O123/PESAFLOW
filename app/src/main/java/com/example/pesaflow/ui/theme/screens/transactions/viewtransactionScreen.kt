package com.example.pesaflow.ui.theme.screens.transactions

import ROUTE_HOME
import ROUTE_LOGIN
import ROUTE_UPDATE_TRANSACTION
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Get current user ID from the transaction view model
    val userId = transactionViewModel.getCurrentUserId()

    // Check if the user is logged in
    LaunchedEffect(Unit) {
        if (userId.isNullOrEmpty()) {
            Toast.makeText(context, "Please login to view transactions", Toast.LENGTH_SHORT).show()
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }
    }

    if (userId.isNullOrEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // MutableState for the currently selected transaction
    val currentTransaction = remember {
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

    // SnapshotStateList for the list of transactions
    val transactionList = remember { mutableStateListOf<TransactionModel>() }
    val filterType = remember { mutableStateOf("All") } // Default filter

    // Load transactions when the Composable is displayed
    LaunchedEffect(userId) {
        transactionViewModel.viewTransactions(currentTransaction, transactionList, context)
    }

    // Function to delete a transaction
    val deleteTransaction: (String) -> Unit = { transactionId ->
        transactionViewModel.deleteTransaction(
            context = context,
            transactionId = transactionId,
            navController = navController
        )
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

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("All", "Income", "Expense").forEach { filter ->
                FilterChip(
                    selected = filterType.value == filter,
                    onClick = { filterType.value = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter and display transactions
        val filteredTransactions = transactionList.filter { txn ->
            when (filterType.value) {
                "All" -> true
                "Income" -> txn.transactionType == "Income"
                "Expense" -> txn.transactionType == "Expense"
                else -> true
            }
        }

        if (filteredTransactions.isEmpty()) {
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
                items(filteredTransactions) { txn ->
                    TransactionItem(
                        transaction = txn,
                        onDeleteClick = { id -> deleteTransaction(id) },
                        onUpdateClick = { id ->
                            println("Navigating to UpdateTransactionScreen with ID: $id")
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
    onDeleteClick: (String) -> Unit,
    onUpdateClick: (String) -> Unit
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
            // Title and Amount Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Ksh ${transaction.amount}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (transaction.transactionType == "Income") Color(0xFF43A047) else Color(0xFFB22222)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Category and Type
            Text(
                text = "Category: ${transaction.category}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Type: ${transaction.transactionType}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Date and Description
            Text(
                text = "Date: ${transaction.date}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Description: ${transaction.description.ifEmpty { "No description" }}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onDeleteClick(transaction.transactionId) },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Delete", color = Color.White)
                }

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