package com.seyi.focuslocktest

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BlockedAppsRepositoryTest {

    @Test
    fun addAndRemoveBlockedApp() {

        val repository = BlockedAppsRepositoryImpl()

        val spotify = "com.spotify.music"

        // Spotify should not initially be blocked
        assertFalse(repository.isBlocked(spotify))

        // Add Spotify
        repository.addBlockedApp(spotify)

        // Spotify should now be blocked
        assertTrue(repository.isBlocked(spotify))

        // Remove Spotify
        repository.removeBlockedApp(spotify)

        // Spotify should no longer be blocked
        assertFalse(repository.isBlocked(spotify))
    }
}