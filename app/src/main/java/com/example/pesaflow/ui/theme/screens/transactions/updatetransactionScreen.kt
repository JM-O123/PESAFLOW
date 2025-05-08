package com.example.pesaflow.ui.theme.screens.transactions

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pesaflow.data.TransactionViewModel
import com.example.pesaflow.models.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun UpdateTransactionScreen(
    navController: NavController,
    transactionId: String
) {
    val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri.value = it }
    }

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val transactionViewModel: TransactionViewModel = viewModel()
    val context = LocalContext.current
    val currentDataRef = FirebaseDatabase.getInstance()
        .getReference("Transactions/$transactionId")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val txn = snapshot.getValue(TransactionModel::class.java)
                txn?.let {
                    amount = it.amount.toString()
                    category = it.category
                    description = it.description
                    date = it.date
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }
        }
        currentDataRef.addValueEventListener(listener)
        onDispose { currentDataRef.removeEventListener(listener) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00BFAE))
                .padding(16.dp)
        ) {
            Text(
                text = "UPDATE TRANSACTION",
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(12.dp))

        // Optional image picker placeholder
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(180.dp)
                .clickable { launcher.launch("image/*") }
        ) { /* empty for now */ }

        Spacer(Modifier.height(8.dp))
        Text(text = "Upload Receipt/Transaction Image")

        Spacer(Modifier.height(16.dp))

        // Amount field
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            placeholder = { Text("Enter amount") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Info, "Amount") }
        )
        Spacer(Modifier.height(8.dp))

        // Category field
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            placeholder = { Text("e.g., Food, Travel") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.ShoppingCart, "Category") }
        )
        Spacer(Modifier.height(8.dp))

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Enter description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            singleLine = false,
            leadingIcon = { Icon(Icons.Default.Edit, "Description") }
        )
        Spacer(Modifier.height(8.dp))

        // Date field
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            placeholder = { Text("yyyy-MM-dd") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.DateRange, "Date") }
        )
        Spacer(Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("CANCEL", color = Color.White)
            }
            Spacer(Modifier.width(20.dp))
            Button(onClick = {
                // Parse amount to Double
                val amt = amount.toDoubleOrNull()
                if (amt == null) {
                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                // Get user ID
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: run {
                        Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                // Update
                transactionViewModel.updateTransaction(
                    context = context,
                    navController = navController,
                    amount = amt,
                    category = category,
                    type = if (category.equals("Food", true)) "Expense" else "Income",
                    title = "Updated Transaction",
                    description = description,
                    transactionId = transactionId,
                    userId = userId,
                    date = date
                )
            }) {
                Text("UPDATE", color = Color.White)
            }
        }
    }
}
