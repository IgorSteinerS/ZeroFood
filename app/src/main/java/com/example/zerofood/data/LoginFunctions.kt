package com.example.zerofood.data

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val auth = FirebaseAuth.getInstance()
@SuppressLint("StaticFieldLeak")
val db = FirebaseFirestore.getInstance()

fun cadastrarUsuario(
    email: String,
    senha: String,
    nome: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, senha)
        .addOnSuccessListener { result ->
            val uid = result.user?.uid ?: ""
            // Salvar dados extras no Firestore
            val usuarioMap = hashMapOf(
                "nome" to nome,
                "email" to email
            )
            db.collection("usuarios").document(uid)
                .set(usuarioMap)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Erro ao salvar dados no Firestore")
                }
        }
        .addOnFailureListener {
            onError(it.message ?: "Erro ao cadastrar usuário")
        }
}

fun loginUsuario(
    email: String,
    senha: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, senha)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener {
            onError(it.message ?: "Erro ao fazer login")
        }
}

fun logout() {
    auth.signOut()
}
