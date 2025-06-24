package com.example.zerofood.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import com.example.zerofood.MainActivity




@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Buscar dados do usuário logado
    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            firestore.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    nome = document.getString("nome") ?: ""
                    email = document.getString("email") ?: ""
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Perfil do Usuário", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Nome: $nome", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Email: $email", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = novaSenha,
                    onValueChange = { novaSenha = it },
                    label = { Text("Nova Senha") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (novaSenha.length >= 6) {
                            user?.updatePassword(novaSenha)
                                ?.addOnSuccessListener {
                                    Toast.makeText(context, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                                    novaSenha = ""
                                }
                                ?.addOnFailureListener {
                                    Toast.makeText(context, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Senha precisa ter ao menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Alterar Senha")
                }
            }

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()

                    // Redirecionar para LoginSignScreen após logout
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Sair", color = Color.White)
            }
        }
    }
}
