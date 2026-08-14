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
import com.seyi.CompletedScreen
import com.seyi.FocusScreen
import com.seyi.IdleScreen
import com.seyi.focuslocktest.ui.theme.FocusLockTestTheme
import kotlinx.coroutines.delay
import java.time.Clock.system

private fun calculateRemainingTime(
    endTime: Long,
    currentTime: Long
): Int {
    return ((endTime - currentTime) / 1000)
        .toInt()
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
    val sessionClock by SessionRepository.sessionClock.collectAsState()
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val context = LocalContext.current
    val storage = remember { SessionStorage(context) }

    val timeRemaining = when (sessionState) {

        SessionState.Focusing ->
            calculateRemainingTime(
                sessionClock.endTime,
                currentTime
            )

        SessionState.Paused ->
            (sessionClock.remainingTime / 1000).toInt()

        else ->
            0
    }

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)


    //This is the recovery system probably after the app got killed
    LaunchedEffect(Unit) {
        val savedState = storage.getSessionState()
        val savedClockValue = storage.getEndTime()

        when (savedState) {

            SessionState.Focusing -> {
                val remainingTime = calculateRemainingTime(
                    savedClockValue,
                    System.currentTimeMillis())

                Log.d(
                    "MainActivity",
                    "RECOVERY: savedState=$savedState, savedClockValue=$savedClockValue"
                )

                if (remainingTime > 0) {
                    SessionRepository.updateClock(
                        SessionClock(
                            endTime = savedClockValue)
                    )
                    SessionRepository.updateSession(SessionState.Focusing)

                    Log.d(
                        "MainActivity",
                        "RECOVERY: remainingTime=$remainingTime"
                    )
                }
                else {
                    storage.clearSession()
                    SessionRepository.updateClock(SessionClock())
                    SessionRepository.updateSession(SessionState.Completed)
                }
            }

            SessionState.Paused -> {
                Log.d(
                    "MainActivity",
                    "RECOVERY: savedState=$savedState, savedClockValue=$savedClockValue")
                if (savedClockValue > 0L) {
                    SessionRepository.updateClock(SessionClock(remainingTime = savedClockValue))
                    SessionRepository.updateSession(SessionState.Paused)
                    Log.d(
                        "MainActivity",
                        "RECOVERY: remainingTime=$savedClockValue"
                    )
                }
                else{
                    storage.clearSession()
                    SessionRepository.updateClock(SessionClock())
                    SessionRepository.updateSession(SessionState.Completed)
                }
            }
            else -> {
                //nothing else to recover
            }
        }

    }

    //the effect responsible for recomposing state
    LaunchedEffect(sessionState) {
        if (sessionState == SessionState.Focusing) {
            while (true) {
                currentTime = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    //Decides which screen to be displayed
    when (sessionState) {

        SessionState.Idle -> {
            IdleScreen(
                time = formattedTime,
                onStartClicked = {
                    //the button responsible for launching background service and focus screen display
                    val intent = Intent(context, FocusService::class.java).apply{
                        action = FocusService.ACTION_START_SESSION
                    }
                    ContextCompat.startForegroundService(context, intent)
                }
            )
        }

        SessionState.Focusing -> {
            FocusScreen(
                time = formattedTime,
                isPaused = false,
                onPauseResumeClicked = {
                    //button responsible for pausing session and drawing the current screen
                    val intent = Intent(
                        context,
                        FocusService::class.java).apply {
                        action = FocusService.ACTION_PAUSE_SESSION
                    }
                    context.startService(intent)
                }
            )
        }

        SessionState.Paused -> {
            FocusScreen(
                time = formattedTime,
                isPaused = true,
                onPauseResumeClicked = {
                    val intent = Intent(
                        context,
                        FocusService::class.java).apply {
                        action = FocusService.ACTION_RESUME_SESSION
                    }
                    context.startService(intent)
                }
            )
        }

        SessionState.Completed -> {
            CompletedScreen(
                onRestartClicked = {
                    val intent = Intent(
                        context,
                        FocusService::class.java).apply {
                        action = FocusService.ACTION_START_SESSION
                    }
                    context.startService(intent)
                }
            )
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