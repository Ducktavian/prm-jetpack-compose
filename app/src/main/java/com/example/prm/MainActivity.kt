package com.example.prm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.prm.ui.personlist.PersonListScreen
import com.example.prm.ui.personlist.PersonListViewModel
import com.example.prm.ui.theme.PRMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRMTheme {
                val personListViewModel = PersonListViewModel()
                PersonListScreen(viewModel = personListViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PRMTheme {
        val personListViewModel = PersonListViewModel()
        PersonListScreen(viewModel = personListViewModel)
    }
}