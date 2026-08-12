package com.seyi.focuslocktest

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionRepository {
    private val _sessionState =
        MutableStateFlow(SessionState.Idle)
    val sessionState = _sessionState.asStateFlow()

    private val _sessionClock = MutableStateFlow(
        SessionClock()
    )
    val sessionClock =
        _sessionClock.asStateFlow()
    fun updateSession(newSession: SessionState) {
        _sessionState.value = newSession
    }

    fun updateClock(newClock:SessionClock){
        _sessionClock.value = newClock
    }
}