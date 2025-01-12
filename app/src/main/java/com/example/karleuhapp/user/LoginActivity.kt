package com.example.karleuhapp.user

import android.content.Intent
import androidx.compose.foundation.border

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.karleuhapp.MainActivity
import com.example.karleuhapp.data.Api
import com.example.karleuhapp.detail.ui.theme.KarleuhAppTheme


class LoginActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.codeAuthFlowFactory.registerActivity(this)
        setContent {
            KarleuhAppTheme {
                LoginScreen(viewModel = viewModel)
            }
        }

        // Observer le résultat de l'authentification
        viewModel.authResult.observe(this) { result ->
            if (result != null) {
                if (result.success) {
                    // Authentification réussie, naviguer vers MainActivity
                    Api.TOKEN = result.accessToken
                    navigateToMainActivity()
                } else {
                    // Authentification échouée, afficher un message d'erreur
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Composable
    fun LoginScreen(viewModel: UserViewModel) {
        // Observer le résultat de l'authentification
        val authResult = viewModel.authResult.value

        // Si l'authentification réussie, naviguer vers MainActivity
        LaunchedEffect(authResult) {
            if (authResult != null) {
                if (authResult.success) {
                    // Authentification réussie, naviguer vers MainActivity
                    navigateToMainActivity()
                } else {
                    // Authentification échouée, afficher un message d'erreur
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login to Your Account", modifier = Modifier.padding(bottom = 20.dp))

            Button(
                onClick = { login() },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Login with OAuth")
            }
        }
    }
    private fun login() {
        // Appeler la méthode login du ViewModel pour démarrer l'authentification
        viewModel.login(this)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Terminer LoginActivity pour éviter que l'utilisateur revienne
    }
}

