package com.example.zerofood.ui.components

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.zerofood.data.ItemCompra
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "shop_list_prefs")

class ShopListDataStore(private val context: Context) {

    private val SHOP_LIST_KEY = stringPreferencesKey("shop_list_json")
    private val gson = Gson()

    // Salva a lista como JSON
    suspend fun saveItems(items: List<ItemCompra>) {
        val json = gson.toJson(items)
        context.dataStore.edit { prefs ->
            prefs[SHOP_LIST_KEY] = json
        }
    }

    // Retorna um Flow da lista, para observar as mudanças
    val itemsFlow: Flow<List<ItemCompra>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[SHOP_LIST_KEY] ?: "[]"
            val type = object : TypeToken<List<ItemCompra>>() {}.type
            gson.fromJson<List<ItemCompra>>(json, type)
        }
}