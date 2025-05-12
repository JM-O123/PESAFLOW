package com.example.pesaflow.ui.theme.screens.transactions

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionScreen(
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val transactionViewModel: TransactionViewModel = viewModel()
    val context = LocalContext.current
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val transactionTypes = listOf("Income", "Expense")

    // Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF006400), // Kenyan green
                        Color(0xFF43A047), // Light green
                        Color(0xFFB22222)  // Kenyan red
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "ADD TRANSACTION",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Transaction Type Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Transaction Type",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        transactionTypes.forEach { type ->
                            FilterChip(
                                selected = transactionType == type,
                                onClick = { transactionType = type },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Input Fields
            CustomInputField(
                value = title,
                onValueChange = { title = it },
                label = "Title"
            )
            CustomInputField(
                value = category,
                onValueChange = { category = it },
                label = "Category"
            )
            CustomInputField(
                value = amount,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' }) amount = it
                },
                label = "Amount (e.g., 123.45)"
            )
            CustomInputField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                height = 120.dp
            )
            CustomInputField(
                value = date.ifEmpty { currentDate },
                onValueChange = { date = it },
                label = "Date"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CANCEL")
                }

                Button(
                    onClick = {
                        if (title.isNotEmpty() && category.isNotEmpty() && amount.isNotEmpty() &&
                            date.isNotEmpty() && transactionType.isNotEmpty()
                        ) {
                            try {
                                isSaving = true
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                                if (userId.isEmpty()) {
                                    Toast.makeText(context, "User authentication error", Toast.LENGTH_SHORT).show()
                                    isSaving = false
                                    return@Button
                                }

                                transactionViewModel.addTransaction(
                                    context = context,
                                    amount = amount,
                                    category = category,
                                    type = transactionType,
                                    date = date.ifEmpty { currentDate },
                                    title = title,
                                    description = description,
                                    navController = navController,
                                    userIdParam = userId
                                )
                                isSaving = false
                            } catch (e: Exception) {
                                isSaving = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("SAVE")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    height: Dp = 56.dp
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(vertical = 8.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 16.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                singleLine = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}