package com.example.zerofood.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.zerofood.data.Item
import com.example.zerofood.ui.components.ItemCard

@Composable
fun ItemList(items: List<Item>){
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            ItemCard(name = item.name, imageRes = item.imageRes)
        }
    }
}