package com.seyi.focuslocktest

import android.app.AlarmManager
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class FocusService: Service() {

    companion object{
        private const val TAG = "FocusService"
        private const val CHANNEL_ID = "focus_lock_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_START_SESSION = "ACTION_START_SESSION"
        const val ACTION_PAUSE_SESSION = "ACTION_PAUSE_SESSION"
        const val ACTION_RESUME_SESSION = "ACTION_RESUME_SESSION"
        const val ACTION_END_SESSION = "ACTION_END_SESSION"
        private const val FOCUS_DURATION = 60 * 1000L

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        createNotificationChannel()
        val notification = createNotification()

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        when (intent?.action) {
            ACTION_START_SESSION -> {
                Log.d(TAG, "Android launched ACTION_START_SESSION")
                startSession()
            }
            ACTION_PAUSE_SESSION -> {
                Log.d(TAG, "Android delivered ACTION_PAUSE_SESSION")
                pauseSession()
            }
            ACTION_RESUME_SESSION -> {
                Log.d(TAG, "Android delivered ACTION_RESUME_SESSION")
                resumeSession()
            }
            ACTION_END_SESSION -> {
                Log.d(TAG, "Android delivered ACTION_END_SESSION")
                endSession()
            }
        }
        return START_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Lock",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus Lock")
            .setContentText("Focus session is active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun createPendingIntent(): PendingIntent {
        Log.d(TAG, "startSession successfully call create pending intent")
        val endSessionIntent = Intent(
            this,
            FocusService::class.java
        ).apply {
            action = ACTION_END_SESSION
        }

        val endSessionPendingIntent =
            PendingIntent.getService(
                this,
                0,
                endSessionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )
            return(endSessionPendingIntent)

    }


    private fun startSession() {
        Log.d(TAG, "startSession successfully called")
        val endTime =
            System.currentTimeMillis() + FOCUS_DURATION

        SessionStorage(this).saveSession(SessionState.Focusing, endTime)

        SessionRepository.updateClock(SessionClock(endTime = endTime))
        SessionRepository.updateSession(SessionState.Focusing)

        val pendingIntent = createPendingIntent()
        val alarmManager = getSystemService(AlarmManager::class.java)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            endTime,
            pendingIntent
        )
        Log.d(TAG, "alarm manager successfully schedule an endTime alarm")
    }

    private fun pauseSession(){
        val endTime = SessionRepository.sessionClock.value.endTime
        val remainingTime = endTime - System.currentTimeMillis()

        if (remainingTime <= 0L){
            endSession()
            return
        }

        SessionStorage(this).saveSession(
            SessionState.Paused, remainingTime
        )

        SessionRepository.updateClock(SessionClock(remainingTime = remainingTime))
        SessionRepository.updateSession(SessionState.Paused)

        val pendingIntent = createPendingIntent()

        val alarmManager =
            getSystemService(AlarmManager::class.java)

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "pause session successfully cancel scheduled alarm")
    }

    private fun resumeSession(){
        Log.d(TAG, "resume session successfully called")
        val remainingTime = SessionRepository.sessionClock.value.remainingTime

        Log.d(TAG, "RESUME: remainingTime = $remainingTime ms")

        if (remainingTime <= 0L){
            endSession()
            return
        }
        val newEndTime = System.currentTimeMillis() + remainingTime

        Log.d(TAG, "RESUME: newEndTime = $newEndTime")

        Log.d(
            TAG,
            "RESUME: duration = ${remainingTime / 1000} seconds"
        )

        SessionStorage(this).saveSession(
            SessionState.Focusing, newEndTime
        )

        SessionRepository.updateClock(SessionClock(endTime = newEndTime))
        SessionRepository.updateSession(SessionState.Focusing)

        val pendingIntent = createPendingIntent()
        val alarmManager = getSystemService(AlarmManager::class.java)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            newEndTime,
            pendingIntent
        )
        Log.d(TAG, "RESUME: alarm rescheduled")
    }
    private fun endSession() {
        Log.d(TAG, "END SESSION: entered endSession()")

        SessionRepository.updateClock(SessionClock())
        SessionRepository.updateSession(SessionState.Completed)


        SessionStorage(this).clearSession()
        stopForeground(STOP_FOREGROUND_REMOVE)

        Log.d(TAG, "END SESSION: stopForeground() called")

        stopSelf()
    }
}