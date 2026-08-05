package com.seyi.focuslocktest

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionRepository {
    private val _sessionState =
        MutableStateFlow(SessionState.Idle)
    val sessionState = _sessionState.asStateFlow()

    private val _endTime =
        MutableStateFlow(0L)

    val endTime =
        _endTime.asStateFlow()

    fun updateSession(newSession: SessionState) {
        _sessionState.value = newSession
    }
    fun updateEndTime(endTime: Long) {
        _endTime.value = endTime
    }
}