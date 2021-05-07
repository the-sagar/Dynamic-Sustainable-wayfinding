package com.dp.logcatapp.services

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService

abstract class BaseService : LifecycleService(){
    private val localBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return localBinder
    }

    inner class LocalBinder : Binder() {
        @Suppress("UNCHECKED_CAST")
        fun <T : BaseService> getService() = this@BaseService as T
    }
}

inline fun <reified T : BaseService> IBinder.getService() = (this as BaseService.LocalBinder).getService<T>()
