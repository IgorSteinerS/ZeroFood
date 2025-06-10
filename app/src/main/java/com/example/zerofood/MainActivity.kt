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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val sampleItems = listOf(
                    Item("Camera", android.R.drawable.ic_menu_camera),
                    Item("Gallery", android.R.drawable.ic_menu_gallery),
                    Item("Delete", android.R.drawable.ic_menu_delete)
                )
                ItemList(items = sampleItems)
            }
        }
    }
}

