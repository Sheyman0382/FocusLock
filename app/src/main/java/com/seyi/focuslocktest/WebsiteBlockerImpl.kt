package com.seyi.focuslocktest

class WebsiteBlockerImpl: WebsiteBlocker {
    override fun startBlocking() {
        println("WEBSITE BLOCKING STARTS")
    }
    override fun stopBlocking() {
        println("WEBSITE BLOCKING STOPPED")
    }
}