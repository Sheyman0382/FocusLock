package com.seyi.focuslocktest

import android.accessibilityservice.AccessibilityService

class BlockingControllerImpl(
    private val accessibilityService: AccessibilityService
) : BlockingController {

    override fun startBlockingApp() {
        println("BLOCKING APP:")
        AppBlockerImpl(accessibilityService).startBlocking()
    }

    override fun stopBlockingApp(){

    }
}