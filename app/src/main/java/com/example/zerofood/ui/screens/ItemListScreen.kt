package com.example.zerofood.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zerofood.data.Item
import com.example.zerofood.ui.components.ItemCard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.zerofood.ui.components.DatePickerDocked
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("RememberReturnType")
@Composable
fun ItemList() {
    val context = LocalContext.current
    var showHiddenPage by remember { mutableStateOf(false) }
    val items = remember { mutableStateListOf<Item>() }
    var selectedItemForEdit by remember { mutableStateOf<Item?>(null) }

    // Opcional: Carrega itens do Firebase quando abre a tela
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("itens")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val fetchedItems = result.mapNotNull { doc ->
                        val nome = doc.getString("nome") ?: return@mapNotNull null
                        val quantidade = doc.getString("quantidade") ?: ""
                        val data = doc.getString("data") ?: ""
                        val imagemBase64 = doc.getString("imagem") ?: ""
                        val id = doc.id
                        Item(id, nome, quantidade, data, imagemBase64) // Certifique-se de que o modelo tem "id"
                    }
                    items.clear()
                    items.addAll(fetchedItems)
                }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedItemForEdit != null) {
            EditItemScreen(
                item = selectedItemForEdit!!,
                onBack = { selectedItemForEdit = null },
                onUpdate = { updatedItem ->
                    val index = items.indexOfFirst { it.id == updatedItem.id }
                    if (index != -1) items[index] = updatedItem
                },
                onDelete = { deletedItem ->
                    items.removeAll { it.id == deletedItem.id }
                }
            )
            return // impede que o restante da tela seja renderizado
        }
        if (showHiddenPage) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = { showHiddenPage = false },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            HiddenPage(
                onItemSaved = { newItem ->
                    items.add(newItem) // Atualiza lista
                    showHiddenPage = false // Fecha formulário
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = { showHiddenPage = !showHiddenPage },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Adicionar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    Box(modifier = Modifier.clickable {
                        selectedItemForEdit = item
                    }) {
                        ItemCard(
                            nome = item.nome,
                            quantidade = item.quantidade,
                            data = item.data,
                            imagemBase64 = item.imagemBase64
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HiddenPage(onItemSaved: (Item) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        DatePickerDocked { selected ->
            data = selected
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .width(140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Imagem",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") }, // ← clique abre a galeria
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Imagem selecionada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Image(
                        painter = painterResource(android.R.drawable.ic_menu_gallery),
                        contentDescription = "Imagem padrão",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { saveItem(context, nome, quantidade, data, imageUri){
                savedItem -> onItemSaved(savedItem)
            } }) {
                Text("Salvar Item")
                Icon(Icons.Default.Add, contentDescription = "Salvar")
            }
        }
    }
}

fun saveItem(
    context: Context,
    nome: String,
    quantidade: String,
    data: String,
    imageUri: Uri?,
    onSuccess: (Item) -> Unit
) {
    if (nome.isBlank() || quantidade.isBlank() || data.isBlank()) {
        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
        return
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        Toast.makeText(context, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance()

    val imageBase64 = imageUri?.let {
        val inputStream = context.contentResolver.openInputStream(it)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
    } ?: ""

    val item = hashMapOf(
        "nome" to nome,
        "quantidade" to quantidade,
        "data" to data,
        "imagem" to imageBase64,
        "userId" to userId
    )

    db.collection("itens")
        .add(item)
        .addOnSuccessListener { documentRef ->
            Toast.makeText(context, "Item salvo com sucesso!", Toast.LENGTH_SHORT).show()
            onSuccess(Item(id = documentRef.id,nome, quantidade, data, imageBase64))
        }
        .addOnFailureListener {
            Toast.makeText(context, "Erro ao salvar item: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

