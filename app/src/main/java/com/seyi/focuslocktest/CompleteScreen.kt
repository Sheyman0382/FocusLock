package com.seyi.focuslocktest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompletedScreen(
    onRestartClicked: () -> Unit
)
{
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFF0D1117)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(text = "🎉 Session Complete!",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = Color.White
        )

        Text(text = "Great job staying focused!",
            color = Color(0xFFB8C0CC),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier  = Modifier.height(16.dp))

        Text(
            text = "● COMPLETED",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRestartClicked)
        {
            Text("Start Another Session")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}