package com.example.pesaflow.ui.theme.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pesaflow.data.AuthViewModel
import com.example.pesaflow.navigations.ROUTE_ADD_TRANSACTION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val selected = remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                NavigationBarItem(
                    selected = selected.intValue == 0,
                    onClick = {
                        selected.intValue = 0
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto: support@pesaflow.com")
                            putExtra(Intent.EXTRA_SUBJECT, "SUPPORT QUERY")
                            putExtra(Intent.EXTRA_TEXT, "Hello, I need assistance with my account.")
                        }
                        context.startActivity(intent)
                    },
                    icon = { Icon(Icons.Filled.Email, contentDescription = "Email Support", modifier = Modifier.size(28.dp)) },
                    label = { Text(text = "Support", style = MaterialTheme.typography.bodyLarge) },
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    selected = selected.value == 1,
                    onClick = {
                        selected.value = 1
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out PesaFlow, the best financial tracking app!")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, null))
                    },
                    icon = { Icon(Icons.Filled.Share, contentDescription = "Share App", modifier = Modifier.size(28.dp)) },
                    label = { Text(text = "Share", style = MaterialTheme.typography.bodyLarge) },
                    alwaysShowLabel = true,
                )
                NavigationBarItem(
                    selected = selected.value == 2,
                    onClick = {
                        selected.value = 2
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:0725987793")
                        }
                        context.startActivity(intent)
                    },
                    icon = { Icon(Icons.Filled.Phone, contentDescription = "Call Support", modifier = Modifier.size(28.dp)) },
                    label = { Text(text = "Call", style = MaterialTheme.typography.bodyLarge) },
                    alwaysShowLabel = true,
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1E1E1E), Color(0xFF3A3A3A)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TopAppBar with icons aligned
                TopAppBar(
                    title = { Text(text = "PesaFlow", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Filled.AccountBox, contentDescription = "Profile", modifier = Modifier.size(28.dp))
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu", modifier = Modifier.size(28.dp))
                        }
                        IconButton(onClick = { authViewModel.logout(navController, context) }) {
                            Icon(imageVector = Icons.Filled.AccountBox, contentDescription = "Log Out", modifier = Modifier.size(28.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp)) // Adds some space before the cards

                // Cards in a grid format with padding between them
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                ) {
                    item {
                        // Add Transaction Card
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { navController.navigate(ROUTE_ADD_TRANSACTION) },
                                shape = RoundedCornerShape(15.dp),
                                elevation = CardDefaults.cardElevation(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF43A047)) // Green card
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(120.dp)
                                        .padding(25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Add Transaction",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }

                            // Budgets Card
                            Card(
                                modifier = Modifier
                                    .weight(1f),
                                shape = RoundedCornerShape(15.dp),
                                elevation = CardDefaults.cardElevation(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)) // Blue card
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(120.dp)
                                        .padding(25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Budgets",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp)) // Adds space between rows
                    }

                    item {
                        // Income Card
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(
                                modifier = Modifier
                                    .weight(1f),
                                shape = RoundedCornerShape(15.dp),
                                elevation = CardDefaults.cardElevation(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF00ACC1)) // Cyan card
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(120.dp)
                                        .padding(25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Income",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }

                            // Expenses Card
                            Card(
                                modifier = Modifier
                                    .weight(1f),
                                shape = RoundedCornerShape(15.dp),
                                elevation = CardDefaults.cardElevation(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935)) // Red card
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(120.dp)
                                        .padding(25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Expenses",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp)) // Adds space between rows
                    }

                    item {
                        // Reports Card
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(
                                modifier = Modifier
                                    .weight(1f),
                                shape = RoundedCornerShape(15.dp),
                                elevation = CardDefaults.cardElevation(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF66BB6A)) // Green card
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(120.dp)
                                        .padding(25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Reports",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    com.example.pesaflow.ui.theme.screens.home.DashboardScreen(rememberNavController())
}
