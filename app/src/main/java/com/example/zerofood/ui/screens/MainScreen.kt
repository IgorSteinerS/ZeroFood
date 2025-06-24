package com.example.zerofood.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zerofood.data.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.zerofood.ui.screens.ProfileScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var items by remember { mutableStateOf<List<Item>>(emptyList()) }

    // Buscar dados ao abrir a tela
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("itens")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val itemList = result.mapNotNull { doc ->
                        val nome = doc.getString("nome") ?: return@mapNotNull null
                        val quantidade = doc.getString("quantidade") ?: return@mapNotNull null
                        val data = doc.getString("data") ?: return@mapNotNull null
                        val imagemBase64 = doc.getString("imagem") ?: ""
                        val id = doc.id
                        Item(id, nome, quantidade, data, imagemBase64)
                    }
                    items = itemList
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao buscar itens: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    val navItems = listOf(
        Triple("home", Icons.Default.Home, "Home"),
        Triple("search", Icons.Filled.Menu, "Compras"),
        Triple("mercados", Icons.Default.LocationOn, "Mercados"),
        Triple("profile", Icons.Default.Person, "Profile")
    )

    Scaffold(
        topBar = {
            Text(
                "ZeroFood",
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                navItems.forEach { (route, icon, label) ->
                    val selected = currentRoute == route
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                ItemList()
            }
            composable("search") { ShopListScreen() }
            composable("mercados") { MercadoMapScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
