package com.example.pesaflow.ui.theme.screens.transactions

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pesaflow.data.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTransactionScreen(
    navController: NavController,
    transactionId: String
) {
    // State variables
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val transactionViewModel: TransactionViewModel = viewModel()
    val context = LocalContext.current

    // Fetch transaction data when screen loads
    LaunchedEffect(transactionId) {
        transactionViewModel.getTransactionById(
            transactionId = transactionId,
            context = context,
            onSuccess = { transaction ->
                amount = transaction.amount.toString()
                category = transaction.category
                description = transaction.description
                date = transaction.date
                transactionType = transaction.transactionType
                title = transaction.title
                isLoading = false
            },
            onFailure = {
                errorMessage = "Failed to load transaction"
                isLoading = false
            }
        )
    }

    // Kenyan-themed gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF006400)) // Kenyan green
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Update Transaction",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Divider(color = Color.White, thickness = 2.dp)

            Spacer(Modifier.height(16.dp))

            // Show loading or error state
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(16.dp)
                    )
                    Text(text = "Loading transaction details...", color = Color.White)
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB22222))
                    ) {
                        Text("Go Back", color = Color.White)
                    }
                }
                else -> {
                    // Transaction Details Form
                    InputField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = "Amount",
                        leadingIcon = Icons.Default.Info
                    )
                    InputField(
                        value = category,
                        onValueChange = { category = it },
                        label = "Category",
                        leadingIcon = Icons.Default.ShoppingCart
                    )
                    InputField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        leadingIcon = Icons.Default.Edit,
                        height = 150.dp
                    )
                    InputField(
                        value = date,
                        onValueChange = { date = it },
                        label = "Date",
                        leadingIcon = Icons.Default.DateRange
                    )

                    Spacer(Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            )
                        ) {
                            Text("CANCEL")
                        }

                        Button(
                            onClick = {
                                val amt = amount.toDoubleOrNull()
                                if (amt == null) {
                                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (category.isBlank() || date.isBlank()) {
                                    Toast.makeText(context, "Required fields are missing", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                transactionViewModel.updateTransaction(
                                    context = context,
                                    navController = navController,
                                    amount = amt,
                                    category = category,
                                    type = transactionType,
                                    title = title.ifEmpty { "Updated Transaction" },
                                    description = description,
                                    transactionId = transactionId,
                                    date = date,
                                    userIdParam = ""
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)) // Kenyan-themed green
                        ) {
                            Text("UPDATE", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    height: Dp = 56.dp,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = label) },
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(vertical = 8.dp),
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}