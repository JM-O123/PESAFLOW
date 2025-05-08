package com.example.pesaflow.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pesaflow.ui.theme.screens.transactions.UpdateTransactionScreen
import com.example.pesaflow.ui.theme.screens.SplashScreen
import com.example.pesaflow.ui.theme.screens.home.DashboardScreen
import com.example.pesaflow.ui.theme.screens.loginScreen.LoginScreen
import com.example.pesaflow.ui.theme.screens.registerscreen.RegisterScreen
import com.example.pesaflow.ui.theme.screens.transactions.AddTransactionScreen
import com.example.pesaflow.screens.ViewTransactionsScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_SPLASH) {
            // Pass the NavHostController directly to SplashScreen
            SplashScreen(navController = navController)
        }

        composable(ROUTE_REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(ROUTE_HOME) {
            DashboardScreen(navController = navController)
        }
        composable(ROUTE_ADD_TRANSACTION) {
            AddTransactionScreen(navController = navController)
        }
        composable(ROUTE_VIEW_TRANSACTIONS) {
            ViewTransactionsScreen(navController = navController)
        }
        composable("$ROUTE_UPDATE_TRANSACTION/{transactionId}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            UpdateTransactionScreen(navController = navController, transactionId = transactionId)
        }
    }
}
