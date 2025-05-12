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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
    val transactionViewModel: TransactionViewModel = viewModel()
    val context = LocalContext.current

    // State
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("") } // For Income/Expense
    var title by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Load the transaction
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

    // Kenyan-themed background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF006400))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(16.dp)
                    )
                    Text("Loading transaction details...", color = Color.White)
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
                    // Input Fields
                    InputField(amount, { amount = it }, "Amount", Icons.Default.Info)
                    InputField(category, { category = it }, "Category", Icons.Default.ShoppingCart)
                    InputField(description, { description = it }, "Description", Icons.Default.Edit, 150.dp)
                    InputField(date, { date = it }, "Date", Icons.Default.DateRange)

                    Spacer(Modifier.height(16.dp))

                    // Transaction Type Selector (Income/Expense)
                    Text(
                        text = "Transaction Type",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Income", "Expense").forEach { type ->
                            FilterChip(
                                selected = transactionType == type,
                                onClick = { transactionType = type },
                                label = { Text(text = type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

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

                                if (category.isBlank() || date.isBlank() || transactionType.isBlank()) {
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
                                    userIdParam = "" // or supply a real user ID
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
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
    leadingIcon: ImageVector? = null, // Optional leading icon
    height: Dp = 56.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    singleLine: Boolean = true, // Default to single-line for most inputs
    maxLines: Int = 1, // Default to 1 line for single-line inputs
    backgroundColor: Color = Color.Transparent, // Background color for better customization
    textColor: Color = Color.White, // Default text color
    labelColor: Color = Color.White.copy(alpha = 0.7f), // Label color
    borderColor: Color = Color.White // Border color for focused and unfocused states
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = labelColor
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = label,
                    tint = textColor
                )
            }
        },
        singleLine = singleLine,
        maxLines = maxLines,
        shape = shape,
        textStyle = TextStyle(color = textColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (singleLine) height else Dp.Unspecified)
            .padding(vertical = 8.dp)
            .background(backgroundColor, shape),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.7f),
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor.copy(alpha = 0.7f),
            cursorColor = textColor,
            focusedLeadingIconColor = textColor,
            unfocusedLeadingIconColor = textColor.copy(alpha = 0.7f)
        )
    )
}