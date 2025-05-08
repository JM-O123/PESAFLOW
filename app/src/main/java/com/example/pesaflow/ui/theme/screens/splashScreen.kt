package com.example.pesaflow.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pesaflow.R
import com.example.pesaflow.navigations.ROUTE_REGISTER
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Launching a delay effect when the splash screen is shown
    LaunchedEffect(Unit) {
        delay(2000L) // 2-second splash duration
        // Navigate to the login screen after delay
        navController.navigate(ROUTE_REGISTER) {
            // Remove splash screen from back stack to avoid returning to it
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    // Splash screen layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo image
            Image(
                painter = painterResource(id = R.drawable.pesaflowlogo),
                contentDescription = "PesaFlow Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // App name text
            Text(
                text = "PesaFlow",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}
