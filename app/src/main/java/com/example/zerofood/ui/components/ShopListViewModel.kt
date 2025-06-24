package com.example.zerofood.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerofood.data.ItemCompra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShopListViewModel(private val dataStore: ShopListDataStore) : ViewModel() {

    private val _itemsCompra = MutableStateFlow<List<ItemCompra>>(emptyList())
    val itemsCompra: StateFlow<List<ItemCompra>> = _itemsCompra

    init {
        viewModelScope.launch {
            dataStore.itemsFlow.collect { lista ->
                _itemsCompra.value = lista
            }
        }
    }

    fun addItem(item: ItemCompra) {
        val novaLista = _itemsCompra.value.toMutableList().apply { add(item) }
        _itemsCompra.value = novaLista
        saveList(novaLista)
    }

    fun removeItem(item: ItemCompra) {
        val novaLista = _itemsCompra.value.toMutableList().apply { remove(item) }
        _itemsCompra.value = novaLista
        saveList(novaLista)
    }

    fun updateItems(newList: List<ItemCompra>) {
        _itemsCompra.value = newList
        saveList(newList)
    }

    private fun saveList(list: List<ItemCompra>) {
        viewModelScope.launch {
            dataStore.saveItems(list)
        }
    }
}
