package com.dp.logcatapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.dp.logcat.Logger

// Bug find/workaround credit: https://github.com/drakeet/ToastCompat#why
fun Context.showToast(msg: CharSequence, length: Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(this, msg, length)
    if (Build.VERSION.SDK_INT <= 25) {
        try {
            val field = View::class.java.getDeclaredField("mContext")
            field.isAccessible = true
            field.set(toast.view, ToastViewContextWrapper(this))
        } catch (e: Exception) {
        }
    }
    toast.show()
}

private class ToastViewContextWrapper(base: Context) : ContextWrapper(base) {
    override fun getApplicationContext(): Context =
            ToastViewApplicationContextWrapper(baseContext.applicationContext)
}

private class ToastViewApplicationContextWrapper(base: Context) : ContextWrapper(base) {
    override fun getSystemService(name: String): Any {
        return if (name == Context.WINDOW_SERVICE) {
            ToastWindowManager(baseContext.getSystemService(name) as WindowManager)
        } else {
            super.getSystemService(name)
        }
    }
}

private class ToastWindowManager(val base: WindowManager) : WindowManager {
    override fun getDefaultDisplay(): Display = base.defaultDisplay

    override fun addView(view: View?, params: ViewGroup.LayoutParams?) {
        try {
            base.addView(view, params)
        } catch (e: WindowManager.BadTokenException) {
            Logger.error("Toast", "caught BadTokenException crash")
        }
    }

    override fun updateViewLayout(view: View?, params: ViewGroup.LayoutParams?) =
            base.updateViewLayout(view, params)

    override fun removeView(view: View?) = base.removeView(view)

    override fun removeViewImmediate(view: View?) = base.removeViewImmediate(view)
}
