package com.seyi.focuslocktest

interface BlockedAppsRepository {

    fun isBlocked(packageName: String): Boolean

    fun getBlockedApps(): Set<String>

    fun addBlockedApp(packageName: String)

    fun removeBlockedApp(packageName: String)
}