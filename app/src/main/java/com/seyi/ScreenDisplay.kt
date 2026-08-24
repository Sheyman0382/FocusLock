package com.seyi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun IdleScreen(
    time: String,
    onStartClicked: () -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "FocusLock (Ready)")

        Text("Make The Choice To Stay Focused Today")

        Text(text = time,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Status: Idle")
        Button(onClick = onStartClicked)
        {
            Text("Activate Mode")
        }
    }
}

@Composable
fun FocusScreen(
    time: String,
    isPaused: Boolean,
    onPauseResumeClicked: () -> Unit
)
{
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            if (isPaused) {
                "FocusLock (Paused)"
            } else {
                "FocusLock (Active)"
            }
        )
        Text("keep Holding On")
        Text(time)

        Text(
            if (isPaused) {
                "Status:Paused"
            } else {
                "Status: Focusing"
            }
        )
        Button(onClick = onPauseResumeClicked) {
            Text(
                if (isPaused) {
                    "Resume"
                } else {
                    "Pause"
                }
            )
        }
    }
}

@Composable
fun CompletedScreen(
    onRestartClicked: () -> Unit
)
{
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 Session Complete!")

        Text("Great job staying focused!")

        Text("Status: Completed")
        Button(onClick = onRestartClicked)
        {
            Text("Start Another Session")
        }
    }
}