package com.seyi.focuslocktest


import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class FocusAccessibilityService : AccessibilityService() {

    private val blockedApps = setOf(
        "com.google.android.youtube",
        "com.whatsapp",
        "com.instagram.android",
        "com.facebook.katana"
    )

    private val blockingController = BlockingControllerImpl(this)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        val packageName = event?.packageName?.toString() ?: return
        val currentState = SessionRepository.sessionState.value

        println("CURRENT APP: $packageName")
        println("ACCESSIBILITY EVENT RECEIVED")

        if (packageName in blockedApps && currentState == SessionState.Focusing) {

            println("BLOCKED APP DETECTED: $packageName")

            blockingController.startBlockingApp()
        }
    }

    override fun onInterrupt() {
        println("ACCESSIBILITY SERVICE INTERRUPTED")
    }
}