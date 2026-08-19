package com.seyi.focuslocktest

class AppBlockerImpl: AppBlocker {
    override fun startBlocking() {
        println("APP BLOCKING STARTED")
    }

    override fun stopBlocking() {
        println("APP BLOCKING STOPPED")
    }
}