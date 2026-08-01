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
        private const val FOCUS_DURATION = 10 * 1000L

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var sessionState = SessionState.Idle
    private var endTime = 0L

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
                startSession()
            }
            ACTION_PAUSE_SESSION -> {

            }
            ACTION_RESUME_SESSION -> {

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

    private fun scheduleSessionEnd() {
        val endSessionIntent = Intent(
            this,
            FocusService::class.java
        ).apply {
            action = ACTION_END_SESSION
        }
        Log.d(TAG, "End session intent created")

        val endSessionPendingIntent =
            PendingIntent.getService(
                this,
                0,
                endSessionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager = getSystemService(AlarmManager::class.java)

        alarmManager.set(AlarmManager.RTC_WAKEUP,
            endTime,
            endSessionPendingIntent
        )
    }

    private fun startSession() {
        sessionState = SessionState.Focusing
        endTime = System.currentTimeMillis() + FOCUS_DURATION

        scheduleSessionEnd()
        }

    private fun endSession() {
        sessionState = SessionState.Completed
        endTime = 0L
        stopSelf()
    }
}