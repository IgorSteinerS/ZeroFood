package com.example.zerofood.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.zerofood.data.ItemCompra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.KeyboardType
import com.example.zerofood.ui.components.ShopListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerofood.data.MyViewModelFactory


@Composable
fun ShopListScreen(viewModel: ShopListViewModel = viewModel(factory = MyViewModelFactory(LocalContext.current))) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val items by viewModel.itemsCompra.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newNome by remember { mutableStateOf("") }
    var newQuantidade by remember { mutableStateOf("") }

    // Modal para adicionar item
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNome.isNotBlank() && newQuantidade.isNotBlank()) {
                            viewModel.addItem(
                                ItemCompra(
                                    nome = newNome,
                                    quantidade = newQuantidade.toIntOrNull() ?: 1
                                )
                            )
                            newNome = ""
                            newQuantidade = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Novo Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newNome,
                        onValueChange = { newNome = it },
                        label = { Text("Nome do Produto") }
                    )
                    OutlinedTextField(
                        value = newQuantidade,
                        onValueChange = { newQuantidade = it },
                        label = { Text("Quantidade") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            }
        )
    }

    // Layout principal
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(56.dp).padding(start = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Adicionar Item",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    if (userId == null) {
                        Toast.makeText(context, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
                        return@IconButton
                    }
                    val marcados = items.filter { it.marcado }
                    marcados.forEach { item ->
                        val itemFirebase = hashMapOf(
                            "nome" to item.nome,
                            "quantidade" to item.quantidade.toString(),
                            "data" to "",
                            "imagem" to "",
                            "userId" to userId
                        )
                        db.collection("itens")
                            .add(itemFirebase)
                            .addOnSuccessListener {
                                Toast.makeText(context, "${item.nome} salvo com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao salvar ${item.nome}", Toast.LENGTH_SHORT).show()
                            }
                        viewModel.removeItem(item)
                    }
                },
                modifier = Modifier.size(56.dp).padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Salvar Selecionados",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Lista dos itens
        items.forEach { item ->
            var checked by remember { mutableStateOf(item.marcado) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            item.marcado = it
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${item.nome} - Quantidade: ${item.quantidade}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.removeItem(item) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remover item"
                        )
                    }
                }
            }
        }
    }
}

