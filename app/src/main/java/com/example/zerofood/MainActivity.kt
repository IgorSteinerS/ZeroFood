package com.example.zerofood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zerofood.data.Item
import com.example.zerofood.ui.screens.ItemList
import com.example.zerofood.ui.screens.LoginSignScreen
import com.example.zerofood.ui.screens.MainScreen
import com.example.zerofood.ui.theme.ZeroFoodTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            ZeroFoodTheme (dynamicColor = false) {
                LoginSignScreen()
            }
        }
    }
}

