package com.seyi.focuslocktest

import android.accessibilityservice.AccessibilityService

class BlockingControllerImpl(
    private val accessibilityService: AccessibilityService
) : BlockingController {
    private val appBlocker = AppBlockerImpl(accessibilityService)

    override fun startBlockingApp() {
        appBlocker.startBlocking()
    }
}