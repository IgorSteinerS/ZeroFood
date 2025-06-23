package com.example.zerofood.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun ShopListScreen() {
    val context = LocalContext.current
    val itemsCompra = listOf(
        ItemCompra("Arroz", 2),
        ItemCompra("Feijão", 1),
        ItemCompra("Macarrão", 3),
    )
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = saveItemShopList(context),
                modifier = Modifier.size(56.dp).padding(start = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Adicionar Item",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { /* TODO: Implement search functionality */ },
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Pesquisar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        itemsCompra.forEach { item ->
            var checked by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.LightGray)
                    .size(60.dp),
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it; item.marcado = checked },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp).clip(RoundedCornerShape(8.dp)),
                )
                Text(
                    text = "${item.nome} - Quantidade: ${item.quantidade}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center).padding(10.dp)
                )
            }
        }
    }
}

fun saveItemShopList(context: Context): () -> Unit {
    return {
        Toast.makeText(context, "Item adicionado à lista de compras", Toast.LENGTH_SHORT).show()
    }
}