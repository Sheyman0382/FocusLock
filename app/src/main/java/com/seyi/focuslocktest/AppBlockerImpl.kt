package com.seyi.focuslocktest

import android.accessibilityservice.AccessibilityService

class AppBlockerImpl(private val accessibilityService: AccessibilityService): AppBlocker {
    override fun startBlocking() {
        println("APP BLOCKING STARTED")
        accessibilityService.performGlobalAction(
            AccessibilityService.GLOBAL_ACTION_HOME)
    }

    override fun stopBlocking() {
        println("APP BLOCKING STOPPED")

    }
}