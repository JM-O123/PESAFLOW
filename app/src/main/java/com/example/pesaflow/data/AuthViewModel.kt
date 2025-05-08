package com.example.pesaflow.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pesaflow.models.TransactionModel
import com.example.pesaflow.models.UserModel
import com.example.pesaflow.navigations.ROUTE_HOME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        // Check if fields are not empty
        if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()) {
            showToast(context, "Please fill all the fields")
            return
        }

        _isLoading.value = true

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    // After user creation, save user data to the database
                    val userId = mAuth.currentUser?.uid.orEmpty()
                    val userData = UserModel(
                        userId = userId,
                        email = email,
                        firstname = firstname
                    )
                    saveUserToDatabase(userId, userData, navController, context)
                } else {
                    _errorMessage.value = task.exception?.message
                    showToast(context, "Registration Failed")
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
                showToast(context, "User Successfully Registered")
                // Navigate to home after successful registration
                navController.navigate(ROUTE_HOME)
            } else {
                _errorMessage.value = task.exception?.message
                showToast(context, "Database Error")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        navController: NavController,
        context: Context
    ) {
        // Ensure email and password are provided
        if (email.isBlank() || password.isBlank()) {
            showToast(context, "Email and password required")
            return
        }

        _isLoading.value = true

        // Sign in the user with email and password
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    // On successful login, navigate to home
                    showToast(context, "User Successfully Logged In")
                    navController.navigate(ROUTE_HOME)
                } else {
                    _errorMessage.value = task.exception?.message
                    showToast(context, "Login Failed")
                }
            }
    }

    fun logout(navController: NavController, context: Context) {
        // Sign out and show toast
        mAuth.signOut()
        showToast(context, "Successfully logged out")
        // Optionally, navigate to login screen if needed
    }

    fun loggedin(): Boolean = mAuth.currentUser != null

    suspend fun getCurrentUser(): UserModel? {
        val userId = mAuth.currentUser?.uid ?: return null
        val ref = FirebaseDatabase.getInstance().getReference("Users/$userId")
        val snapshot = ref.get().await()
        return snapshot.getValue(UserModel::class.java)
    }

    fun saveTransaction(
        title: String,
        amount: Double,
        category: String,
        description: String,
        transactionType: String,
        navController: NavController,
        context: Context
    ) {
        val userId = mAuth.currentUser?.uid ?: run {
            showToast(context, "Not logged in")
            return
        }

        if (title.isBlank() || category.isBlank() || description.isBlank()) {
            showToast(context, "Please fill all transaction fields")
            return
        }

        val transactionId = FirebaseDatabase.getInstance().reference.push().key ?: return
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val transaction = TransactionModel(
            transactionId = transactionId,
            title = title,
            amount = amount,
            category = category,
            description = description,
            transactionType = transactionType,
            userId = userId,
            date = date
        )

        val ref = FirebaseDatabase.getInstance().getReference("transactions/$userId/$transactionId")
        ref.setValue(transaction).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast(context, "Transaction saved")
                navController.navigate(ROUTE_HOME)
            } else {
                _errorMessage.value = task.exception?.message
                showToast(context, "Failed to save transaction")
            }
        }
    }}
