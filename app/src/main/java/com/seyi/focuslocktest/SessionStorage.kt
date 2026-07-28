package com.seyi.focuslocktest

import android.content.Context

class SessionStorage(context: Context) {

    private val prefs =
        context.getSharedPreferences("focuslock_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SESSION_STATE = "session_state"
        private const val KEY_END_TIME = "end_time"
    }

    fun saveSession(state: SessionState, endTime: Long) {
        prefs.edit()
            .putString(KEY_SESSION_STATE, state.name)
            .putLong(KEY_END_TIME, endTime)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_SESSION_STATE)
            .remove(KEY_END_TIME)
            .apply()
    }

    fun getSessionState(): SessionState? {
        val value = prefs.getString(KEY_SESSION_STATE, null)

        return value?.let {
            try {
                SessionState.valueOf(it)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }

    fun getEndTime(): Long {
        return prefs.getLong(KEY_END_TIME, 0L)
    }
}