package com.seyi.focuslocktest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.seyi.focuslocktest.ui.theme.FocusLockTestTheme
import kotlinx.coroutines.delay
import java.time.Clock.system

private fun calculateRemainingTime(
    endTime: Long
): Int {
    return ((endTime - System.currentTimeMillis()) / 1000).toInt()
}

class MainActivity : ComponentActivity() {
    private lateinit var storage: SessionStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = SessionStorage(this)
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

@Composable
fun FocusLockScreen(modifier: Modifier = Modifier) {
    val sessionState by SessionRepository.sessionState.collectAsState()
    val endTime by SessionRepository.endTime.collectAsState()
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val timeRemaining = calculateRemainingTime(endTime)


    val context = LocalContext.current
    val storage = remember { SessionStorage(context) }


    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    LaunchedEffect(Unit) {
        val savedState = storage.getSessionState()
        val savedEndTime = storage.getEndTime()
        val remaining = calculateRemainingTime(savedEndTime)

        if (
            savedState == SessionState.Focusing ||
            savedState == SessionState.Paused
        ) {
            if (remaining > 0) {
                SessionRepository.updateSession(savedState)
                SessionRepository.updateEndTime(savedEndTime)

            } else {

                storage.clearSession()
                SessionRepository.updateSession(SessionState.Completed)
            }
        }
    }


    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }


    when (sessionState) {

        SessionState.Idle -> {
            IdleScreen(
                time = formattedTime,
                onStartClicked = {
                    val intent = Intent(context, FocusService::class.java)
                    intent.action = FocusService.ACTION_START_SESSION
                    ContextCompat.startForegroundService(context, intent)
                }
            )
        }

        SessionState.Focusing -> {
            FocusScreen(
                time = formattedTime,
                isPaused = false,
                onPauseResumeClicked = {
                    storage.saveSession(
                        SessionState.Paused,
                        storage.getEndTime()
                    )
                    SessionRepository.updateSession(SessionState.Paused)
                }
            )
        }

        SessionState.Paused -> {
            FocusScreen(
                time = formattedTime,
                isPaused = true,
                onPauseResumeClicked = {
                    storage.saveSession(
                        SessionState.Focusing,
                        storage.getEndTime()
                    )
                    SessionRepository.updateSession(SessionState.Focusing)

                }
            )

        }

        SessionState.Completed -> {
            CompletedScreen(
                onRestartClicked = {
                    storage.clearSession()

                    SessionRepository.updateSession(SessionState.Idle)
                }
            )
        }

    }
}
@Composable
fun IdleScreen(
    time: String,
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

        Text(time)

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
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Blue),
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