package com.seyi.focuslocktest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.seyi.focuslocktest.ui.theme.FocusLockTestTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusLockTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FocusLockScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class SessionState {
    Idle,
    Focusing,
    Paused,
    Completed
}

@Composable
fun FocusLockScreen(modifier: Modifier = Modifier) {
    var sessionState by rememberSaveable { mutableStateOf(SessionState.Idle) }
    var timeRemaining by rememberSaveable { mutableStateOf(1500) }

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    LaunchedEffect(sessionState)
    {
        if (sessionState == SessionState.Focusing) {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
            }
            sessionState = SessionState.Completed
        }
    }


    when (sessionState) {
        SessionState.Idle -> {
            IdleScreen(
                onStartClicked = {
                    sessionState = SessionState.Focusing
                }
            )
        }

        SessionState.Focusing -> {
            FocusScreen(
                time = formattedTime,
                isPaused = false,
                onPauseResumeClicked = {
                    sessionState = SessionState.Paused
                }
            )
        }

        SessionState.Paused -> {
            FocusScreen(
                time = formattedTime,
                isPaused = true,
                onPauseResumeClicked = {
                    sessionState = SessionState.Focusing
                }
            )

        }

        SessionState.Completed -> {
            CompletedScreen(
                onRestartClicked = {
                    timeRemaining = 1500
                    sessionState = SessionState.Idle
                }
            )
        }

    }
}
@Composable
fun IdleScreen(
    onStartClicked: () -> Unit
)
{
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("FocusLock (Ready)")

        Text("Make The Choice To Stay Focused Today")

        Text("25:00")

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

@Preview(showBackground = true)
@Composable
fun FocusLockScreenPreview() {
    FocusLockTestTheme {
        FocusLockScreen()
    }
}