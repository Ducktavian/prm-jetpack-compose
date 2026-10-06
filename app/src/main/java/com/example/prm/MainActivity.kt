package com.example.prm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DemoScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            Text(
                text = "Demo Screen",
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Demo Button")


            }
            PersonCard()
        }
    }

    @Composable
    fun PersonCard() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = Color.Black)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(0.4f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                CircularAvatar("AR", Modifier)

                // Vertical [Name, and info]
                Column(
                    modifier = Modifier
                ) {
                    Text(text = "Alex Rivera")
                    Text(text = "Classmate")
                }

            }
            Button(onClick = { /*TODO*/ }) {
                
            }
        }

    }


    @Composable
    fun CircularAvatar(
        initials: String,
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier = modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF6200EE), shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}
