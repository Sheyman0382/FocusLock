package com.seyi.focuslocktest


import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class FocusAccessibilityService : AccessibilityService() {
    private val blockingController = BlockingControllerImpl(this)
    private val blockedAppsRepository = BlockedAppsRepositoryImpl()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        val packageName = event?.packageName?.toString() ?: return
        val currentState = SessionRepository.sessionState.value

        println("CURRENT APP: $packageName")
        println("ACCESSIBILITY EVENT RECEIVED")
        println("EVENT TEXT: ${event.text}")

        if (blockedAppsRepository.isBlocked(packageName) && currentState == SessionState.Focusing) {

            println("BLOCKED APP DETECTED: $packageName")
            blockingController.startBlockingApp()
        }
    }

    override fun onInterrupt() {
        println("ACCESSIBILITY SERVICE INTERRUPTED")
    }
}