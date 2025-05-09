package com.example.pesaflow.ui.theme.screens.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pesaflow.data.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddTransactionScreen(
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }

    val transactionViewModel: TransactionViewModel = viewModel()
    val context = LocalContext.current
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val typeOptions = listOf("Income", "Expense")

    // Wrapping the entire content inside a Column with a vertical scroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ADD TRANSACTION",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction Type Dropdown
        // You can implement dropdown here if needed, or use a simple Toggle or Radio Button for type selection

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            placeholder = { Text("yyyy-MM-dd") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                IconButton(onClick = {
                    date = currentDate
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Set current date")
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("dashboard") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("DASHBOARD")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    // Validate if all fields are filled
                    if (name.isNotEmpty() && category.isNotEmpty() && amount.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty()) {
                        // Parse the amount to double and check if it's valid
                        val parsedAmount = amount.toDoubleOrNull()
                        if (parsedAmount != null) {
                            // Get the current user's ID (e.g., from Firebase Auth)
                            val userId = "your_user_id_here" // You need to implement fetching the user ID

                            // Call the addTransaction function to save data
                            transactionViewModel.addTransaction(
                                context = context,
                                amount = parsedAmount.toString(),
                                category = category,
                                type = type,
                                date = date,
                                title = name,
                                description = description,
                                userId = userId,
                                navController = navController
                            )

                            // Show success message
                            Toast.makeText(context, "$type Saved", Toast.LENGTH_SHORT).show()
                            // Navigate back to the dashboard
                            navController.navigate("dashboard")
                        } else {
                            Toast.makeText(context, "Invalid amount", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE")
            }
        }
    }
}
