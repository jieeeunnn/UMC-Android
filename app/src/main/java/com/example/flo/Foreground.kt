package com.example.flo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class Foreground : Service() {

    val CHANNEL_ID = "FGS"
    val NOTI_ID = 153
    private val PROGRESS_MAX = 1000
    private var progress = 0

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "FOREGROUND", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Flo Foreground Service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent) // 알림 클릭 시 실행될 액티비티 설정
            .setProgress(PROGRESS_MAX, progress, false)
            .build()

        startForeground(NOTI_ID, notification)

        runBackground()

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun runBackground() {
        val localPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        thread(start = true) {
            for (i in 0..PROGRESS_MAX) {
                Thread.sleep(1000)
                Log.d("서비스", "COUNT==> $i")
                progress = i
                updateNotificationProgress(localPendingIntent)
            }
        }
    }
    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateNotificationProgress(pendingIntent: PendingIntent) {
        val notificationManager =
            getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Flo Foreground Service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setProgress(PROGRESS_MAX, progress, false)
            .build()

        notificationManager.notify(NOTI_ID, notification)
    }
}
