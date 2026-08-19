package com.seyi.focuslocktest

interface AppBlocker {

    fun startBlocking() {
        println("right inside app blocker interface")
    }

    fun stopBlocking()
}