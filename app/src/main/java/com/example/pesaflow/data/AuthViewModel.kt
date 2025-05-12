package com.example.pesaflow.data

import ROUTE_HOME
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pesaflow.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> get() = _isLoading
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun signup(
        firstname: String,
        lastname: String,
        email: String,
        password: String,
        navController: NavController,
        context: Context
    ) {
        if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()) {
            showToast(context, "Please fill all the fields")
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "User created successfully: ${mAuth.currentUser?.uid}")
                    val userId = mAuth.currentUser?.uid.orEmpty()
                    val userData = UserModel(
                        userId = userId,
                        email = email,
                        firstname = firstname,
                        lastname = lastname
                    )
                    saveUserToDatabase(userId, userData, navController, context)
                } else {
                    _errorMessage.value = task.exception?.message
                    Log.e("AuthViewModel", "Authentication failed", task.exception)
                    showToast(context, _errorMessage.value ?: "Registration Failed")
                }
            }
    }

    private fun saveUserToDatabase(
        userId: String,
        userData: UserModel,
        navController: NavController,
        context: Context
    ) {
        val ref = FirebaseDatabase.getInstance().getReference("Users/$userId")
        ref.setValue(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AuthViewModel", "User data saved to database")
                showToast(context, "User Successfully Registered")
                navController.navigate(ROUTE_HOME) {
                    popUpTo("register") { inclusive = true }
                }
            } else {
                _errorMessage.value = task.exception?.message
                Log.e("AuthViewModel", "Database save failed", task.exception)
                showToast(context, "Database Error: ${_errorMessage.value}")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        navController: NavController,
        context: Context
    ) {
        if (email.isBlank() || password.isBlank()) {
            showToast(context, "Email and password required")
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "User logged in successfully: ${mAuth.currentUser?.uid}")
                    showToast(context, "User Successfully Logged In")
                    navController.navigate(ROUTE_HOME) {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    _errorMessage.value = task.exception?.message
                    Log.e("AuthViewModel", "Login failed", task.exception)
                    showToast(context, "Login Failed: ${_errorMessage.value}")
                }
            }
    }

    fun logout(navController: NavController, context: Context) {
        mAuth.signOut()
        showToast(context, "Successfully logged out")
        navController.navigate("login") {
            popUpTo(0) // Clear the navigation stack
        }
    }

    fun loggedin(): Boolean = mAuth.currentUser != null

    suspend fun getCurrentUser(): UserModel? {
        val userId = mAuth.currentUser?.uid ?: return null
        val ref = FirebaseDatabase.getInstance().getReference("Users/$userId")
        return try {
            val snapshot = ref.get().await()
            snapshot.getValue(UserModel::class.java)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error fetching user data", e)
            null
        }
    }
}