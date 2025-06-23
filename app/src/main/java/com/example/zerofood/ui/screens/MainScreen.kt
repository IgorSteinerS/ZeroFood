package com.example.zerofood.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zerofood.data.Item

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val navItems = listOf(
        Triple("home", Icons.Default.Home, "Home"),
        Triple("search", Icons.Filled.Menu, "Compras"),
        Triple("mercados", Icons.Default.LocationOn, "Mercados"),
        Triple("profile", Icons.Default.Person, "Profile")
    )

    val sampleItems = listOf(
        Item("Camera", android.R.drawable.ic_menu_camera),
        Item("Gallery", android.R.drawable.ic_menu_gallery),
        Item("Delete", android.R.drawable.ic_menu_delete),
        Item("Delete", android.R.drawable.ic_menu_delete)
    )

    Scaffold(
        topBar = {
            Text("ZeroFood", modifier = Modifier.padding(16.dp).fillMaxWidth(), fontSize = 40.sp, textAlign = TextAlign.Center)
        },
        bottomBar = {
            NavigationBar (
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                navItems.forEach { (route, icon, label) ->
                    val selected = currentRoute == route // 🔒 Só seleciona rota exata
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
                ItemList(sampleItems)
            }
            composable("search") { ShopListScreen() }
            composable("mercados") { ProfileScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

@Composable fun ProfileScreen() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Tela Perfil") } }
