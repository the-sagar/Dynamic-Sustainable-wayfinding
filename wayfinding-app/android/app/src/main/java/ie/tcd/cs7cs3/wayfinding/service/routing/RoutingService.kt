package ie.tcd.cs7cs3.wayfinding.service.routing

import android.annotation.TargetApi
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.github.tegola.mobile.controller.utils.SysUtil
import java.io.File

class RoutingService : Service() {
    companion object {
        val TAG = RoutingService::class.qualifiedName
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class ServiceIntents {
        ROUTING_COMMAND_STOP,
        ROUTING_COMMAND_START;
    }

    inline fun <reified T : Enum<*>> enumValueOrNull(name: String?): T? =
        T::class.java.enumConstants.firstOrNull { it.name == name }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = enumValueOrNull<ServiceIntents>(intent?.action) ?: return START_STICKY
        when(action) {
            ServiceIntents.ROUTING_COMMAND_STOP -> {
                Log.d(TAG, "onStartCommand: Received STOP request")
                Shell.instance.shell_stop()
//                stopForeground(true)
                stopSelfResult(startId)
            }
            ServiceIntents.ROUTING_COMMAND_START -> {
                val binExe = File(applicationContext.applicationInfo.nativeLibraryDir, "librouting.so")
                val env = HashMap<String, String>()
                env.put("LD_LIBRARY_PATH", SysUtil.LD_LIBRARY_PATH(applicationContext))
                val routingDataPath = File(applicationContext.getExternalFilesDir(null), "routing-data")
                if (!routingDataPath.exists())
                    routingDataPath.mkdirs()
                env.put("OSMROUTEDATADIR", routingDataPath.path)
                val shell_res = Shell.instance.shell_start(binExe.path, env)
                if (!shell_res) {
                    Log.w(TAG, "onStartCommand: shell_start failed on starting: ${binExe.path}")
                    Shell.instance.shell_stop()
//                    stopForeground(true)
                    stopSelfResult(startId)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Shell.instance.shell_stop()
//        Log.d(TAG, "onDestroy: stopForeground")
//        stopForeground(true)
        if (Build.VERSION.SDK_INT >= 26) {
            deleteNotificationChannel()
        }
        Log.d(TAG, "onDestroy: stopSelf")
        stopSelf()
    }

    @TargetApi(26)
    private fun deleteNotificationChannel() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.deleteNotificationChannel("Routing Foreground Service")
    }
}
