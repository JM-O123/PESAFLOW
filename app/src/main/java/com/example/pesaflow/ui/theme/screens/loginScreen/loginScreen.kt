package com.example.pesaflow.ui.theme.screens.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pesaflow.R
import com.example.pesaflow.data.AuthViewModel
import com.example.pesaflow.navigations.ROUTE_REGISTER

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize() // Ensures the Box takes the full screen
            .padding(20.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Black, Color(0xFFB50000), Color(0xFF1A3C00)) // black, dark red, dark green
                )
            )
    ) {
        // Back button at the top-left corner
        Button(
            onClick = { /* handle back action */ },
            colors = ButtonDefaults.buttonColors(containerColor = White),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Back icon if needed
        }

        // Circular image centered, with padding
        Image(
            painter = painterResource(id = R.drawable.pesaflow), // Replace with your actual image resource
            contentDescription = "Circular Image",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        // Main content
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome text at the top
            Text(
                text = "Welcome Back!",
                color = White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { newEmail -> email = newEmail },
                label = { Text(text = "Email Address") },
                placeholder = { Text(text = "Enter your email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(50)
            )

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = { newPassword -> password = newPassword },
                label = { Text(text = "Enter Password") },
                placeholder = { Text(text = "Enter your password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(50)
            )

            // Forgot password link
            Text(
                text = "Forgot password?",
                color = White,
                modifier = Modifier
                    .clickable {
                        // Handle forgot password action
                    }
                    .padding(bottom = 16.dp)
            )

            // Login button with teal color
            Button(
                onClick = { authViewModel.login(email, password, navController, context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008080)), // Teal
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 18.sp,
                    color = White
                )
            }

            // Google sign-in button with red color
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437)),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Sign in with Google",
                    fontSize = 18.sp,
                    color = White
                )
            }

            // Twitter sign-in button with blue color
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DA1F2)),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Sign in with Twitter",
                    fontSize = 18.sp,
                    color = White
                )
            }

            // Register link at the bottom
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = White, fontWeight = FontWeight.Bold)) {
                        append("Don't have an account? ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF008080))) {
                        append("Sign Up")
                    }
                },
                modifier = Modifier
                    .clickable {
                        navController.navigate(ROUTE_REGISTER)
                    }
                    .padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}
