package com.example.zerofood.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zerofood.data.Item
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditItemScreen(
    item: Item,
    onBack: () -> Unit,
    onUpdate: (Item) -> Unit,
    onDelete: (Item) -> Unit
) {
    val context = LocalContext.current
    var nome by remember { mutableStateOf(item.nome) }
    var quantidade by remember { mutableStateOf(item.quantidade) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = MaterialTheme.colorScheme.primary, contentDescription = "Voltar")
        }

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable { launcher.launch("image/*") }
                .size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                imageUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Nova imagem",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    )
                }
                item.imagemBase64.isNotBlank() -> {
                    val imageBytes = Base64.decode(item.imagemBase64, Base64.DEFAULT)
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagem atual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    )
                }
                else -> {
                    Icon(Icons.Default.Add, contentDescription = "Imagem")
                }
            }
        }

        Button(onClick = {
            updateItem(context, item.id, nome, quantidade, imageUri) { updatedItem ->
                onUpdate(updatedItem)
                onBack()
            }
        }) {
            Text("Salvar")
        }

        Button(
            onClick = {
                deleteItem(context, item.id) {
                    onDelete(item)
                    onBack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Excluir", color = Color.White)
        }
    }
}


fun updateItem(
    context: Context,
    itemId: String,
    nome: String,
    quantidade: String,
    imageUri: Uri?,
    onSuccess: (Item) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val imagemBase64 = imageUri?.let {
        val stream = context.contentResolver.openInputStream(it)
        val bytes = stream?.readBytes()
        stream?.close()
        bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
    }

    val updates = mutableMapOf<String, Any>(
        "nome" to nome,
        "quantidade" to quantidade,
    )

    imagemBase64?.let {
        updates["imagem"] = it
    }

    db.collection("itens").document(itemId)
        .update(updates)
        .addOnSuccessListener {
            Toast.makeText(context, "Item atualizado!", Toast.LENGTH_SHORT).show()
            onSuccess(Item(itemId, nome, quantidade, "", imagemBase64 ?: ""))
        }
        .addOnFailureListener {
            Toast.makeText(context, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

fun deleteItem(
    context: Context,
    itemId: String,
    onDeleted: (String) -> Unit
) {
    FirebaseFirestore.getInstance().collection("itens")
        .document(itemId)
        .delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Item excluído", Toast.LENGTH_SHORT).show()
            onDeleted(itemId)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}