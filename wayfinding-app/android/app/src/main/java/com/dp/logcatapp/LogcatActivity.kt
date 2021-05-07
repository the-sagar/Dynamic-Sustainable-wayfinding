package com.dp.logcatapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
//import android.view.Menu
//import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.dp.logcatapp.fragments.logcatlive.LogcatLiveFragment
import com.dp.logcatapp.services.LogcatService
import ie.tcd.cs7cs3.wayfinding.R

class LogcatActivity : BaseActivityWithToolbar() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logcat)
        setupToolbar()

        val logcatServiceIntent = Intent(this, LogcatService::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(logcatServiceIntent)
        } else {
            startService(logcatServiceIntent)
        }

        if (savedInstanceState == null) {
//            val stopRecording = intent?.getBooleanExtra(STOP_RECORDING_EXTRA,
//                    false) == true
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LogcatLiveFragment.newInstance(false),
                            LogcatLiveFragment.TAG)
                    .commit()
        }
    }

    override fun getToolbarIdRes(): Int = R.id.toolbar

    override fun getToolbarTitle(): String = getString(R.string.logcat)

    override fun onDestroy() {
        super.onDestroy()
//        stopService(Intent(this, LogcatService::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG = LogcatActivity::class.qualifiedName
    }
}
