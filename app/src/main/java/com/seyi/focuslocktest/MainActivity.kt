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

@Composable
fun FocusLockScreen(modifier: Modifier = Modifier)
{
    var isFocusing by rememberSaveable { mutableStateOf(false) }
    var timeRemaining by rememberSaveable { mutableStateOf( 1500)}

    LaunchedEffect(isFocusing)
    {
        if (isFocusing)
        {
            while (timeRemaining > 0)
            {
                delay(1000)
                timeRemaining--
            }
        }
    }


    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isFocusing)
            { "FocusLock (Active)" }
            else
            { "FocusLock (Ready)" }
        )

        Text(text = if (isFocusing)
            {"keep Holding On!!!"}
            else{"Make The Choice To Stay Focused Today"}
        )

        Text(text = formattedTime)

        Text(
            text = if (isFocusing)
            { "Status: Focusing" }
            else
            { "Status: Idle" }
        )
        Button(onClick = {isFocusing = true})
        {
            Text(
                text = if (isFocusing)
                { "Focus Mode Activated" }
                else
                { "Activate Mode" }
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