package com.dp.logcatapp.services

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dp.logcat.Logcat
import com.dp.logcatapp.LogcatActivity
import ie.tcd.cs7cs3.wayfinding.R

class LogcatService : BaseService() {

    companion object {
        val TAG = LogcatService::class.qualifiedName
        private const val NOTIFICATION_CHANNEL = "logcat_channel_01"
        private const val NOTIFICATION_ID = 1
    }

    lateinit var logcat: Logcat
        private set
    var restartedLogcat = false

    var paused = false

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel()
        }

        initLogcat()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val startIntent = Intent(this, LogcatActivity::class.java)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        val contentIntent = PendingIntent.getActivity(this, 0, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_perm_device_information_white)
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setContentTitle(getString(R.string.logcat_service))
                .setTicker(getString(R.string.app_name))
//                .setContentText(getString(R.string.logcat_service))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setAutoCancel(false)
                .setOngoing(true)

        return builder.build()
    }

    @TargetApi(26)
    private fun createNotificationChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val nc = NotificationChannel(NOTIFICATION_CHANNEL,
                getString(R.string.logcat_service_channel_name), NotificationManager.IMPORTANCE_MIN)
        nc.enableLights(false)
        nc.enableVibration(false)
        nc.setShowBadge(false)
        nm.createNotificationChannel(nc)
    }

    @TargetApi(26)
    private fun deleteNotificationChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.deleteNotificationChannel(NOTIFICATION_CHANNEL)
    }

    override fun onDestroy() {
        stopForeground(true);
        super.onDestroy()
        logcat.close()
    }

    private fun initLogcat() {
        val maxLogs = 250_000
        val pollInterval = 250

        logcat = Logcat(maxLogs)
        logcat.setPollInterval(pollInterval.toLong())

        val buffers = Logcat.AVAILABLE_BUFFERS

        fun getDefaultBufferValues(): Set<String> {
            val bufferValues = mutableSetOf<String>()
            Logcat.DEFAULT_BUFFERS.map { Logcat.AVAILABLE_BUFFERS.indexOf(it) }
                    .filter { it != -1 }
                    .forEach { bufferValues += it.toString() }
            return bufferValues
        }

        logcat.logcatBuffers = getDefaultBufferValues().map { e -> buffers[e.toInt()].toLowerCase() }.toSet()
        logcat.start()
    }
}
