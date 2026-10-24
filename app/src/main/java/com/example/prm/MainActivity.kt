package com.example.prm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.prm.navigation.AppNavHost
import com.example.prm.ui.theme.PRMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRMTheme {
                AppNavHost()
            }
        }
    }
}
