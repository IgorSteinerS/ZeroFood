package com.example.zerofood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.zerofood.data.cadastrarUsuario
import com.example.zerofood.data.loginUsuario

@Composable
fun LoginSignScreen() {
    var isLogin by remember { mutableStateOf(true) }
    var loggedIn by remember { mutableStateOf(false) }

    if (loggedIn) {
        MainScreen()
    } else {
        if (isLogin) {
            Login(
                onToggle = { isLogin = false },
                onLoginSuccess = { loggedIn = true }
            )
        } else {
            Cadastro(
                onToggle = { isLogin = true },
                onCadastroSuccess = { loggedIn = true }
            )
        }
    }
}

@Composable
fun Login(
    onToggle: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ZeroFood", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                erro = null
                loading = true
                loginUsuario(email, senha,
                    onSuccess = {
                        loading = false
                        onLoginSuccess()
                    },
                    onError = {
                        loading = false
                        erro = it
                    })
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = !loading
        ) {
            Text(if (loading) "Entrando..." else "Login")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onToggle) {
            Text("Não tem conta? Cadastre-se")
        }

        erro?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun Cadastro(
    onToggle: () -> Unit,
    onCadastroSuccess: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ZeroFood", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                erro = null
                loading = true
                cadastrarUsuario(email, senha, nome,
                    onSuccess = {
                        loading = false
                        onCadastroSuccess()
                    },
                    onError = {
                        loading = false
                        erro = it
                    })
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = !loading
        ) {
            Text(if (loading) "Cadastrando..." else "Cadastrar")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onToggle) {
            Text("Já tem conta? Faça login")
        }

        erro?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
