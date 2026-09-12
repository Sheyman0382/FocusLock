package com.seyi.focuslocktest

class BlockedAppsRepositoryImpl: BlockedAppsRepository{
    private val blockedApps = mutableSetOf(
        "com.google.android.youtube",
        "com.whatsapp",
        "com.instagram.android",
        "com.facebook.katana"
    )
    override fun isBlocked(packageName: String): Boolean {

        return packageName in blockedApps
    }

    override fun getBlockedApps(): Set<String> {
        return blockedApps
    }

    override fun addBlockedApp(packageName: String) {
        blockedApps.add(packageName)
    }
    override fun removeBlockedApp(packageName: String) {
        blockedApps.remove(packageName)
    }
}