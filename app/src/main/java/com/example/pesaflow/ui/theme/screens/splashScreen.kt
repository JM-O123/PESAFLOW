package com.example.pesaflow.ui.theme.screens

import ROUTE_REGISTER
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pesaflow.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Animation state for scaling the logo
    val scale = remember { Animatable(0f) }

    // Launching animations and navigation logic
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000) // 1 second animation
        )
        delay(2000L) // 2-second splash duration
        navController.navigate(ROUTE_REGISTER) {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    // Gradient background and layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF006400), // Kenyan green
                        Color(0xFFB22222), // Kenyan red
                        Color.Black // Black for a smooth transition
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo
            Image(
                painter = painterResource(id = R.drawable.pesaflowlogo),
                contentDescription = "PesaFlow Logo",
                modifier = Modifier
                    .size((120 * scale.value).dp) // Apply scaling animation
            )
            Spacer(modifier = Modifier.height(16.dp))

            // App name with gradient text
            Text(
                text = "PesaFlow",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline for the app
            Text(
                text = "Manage Your Finances With Ease",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}