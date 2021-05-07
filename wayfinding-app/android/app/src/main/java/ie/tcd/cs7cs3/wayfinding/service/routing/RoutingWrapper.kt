package ie.tcd.cs7cs3.wayfinding.service.routing

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class RoutingWrapper(context: ReactApplicationContext): ReactContextBaseJavaModule(context),
        Shell.ServiceCallbackListener {

    companion object {
        lateinit var reactContext: ReactApplicationContext
    }

    init {
        reactContext = context
    }

    override fun getName(): String {
        return "RoutingService"
    }

    @ReactMethod
    fun startService() {
        val intent = Intent(reactContext, RoutingService::class.java)
        intent.action = "ROUTING_COMMAND_START"
        reactContext.startService(intent)
    }

    @ReactMethod
    fun stopService() {
        val intent = Intent(reactContext, RoutingService::class.java)
        intent.action = "ROUTING_COMMAND_STOP"
        reactContext.startService(intent)
    }

    override fun PreStart() {
        TODO("Not yet implemented")
    }

    override fun StartFailed(reason: String) {
        TODO("Not yet implemented")
    }

    override fun Running(port: Int, pid: Int) {
        TODO("Not yet implemented")
    }

    override fun OutputStdout(line: String) {
        TODO("Not yet implemented")
    }

    override fun OutputStderr(line: String) {
        TODO("Not yet implemented")
    }

    override fun PreStop() {
        TODO("Not yet implemented")
    }

    override fun PostStop() {
        TODO("Not yet implemented")
    }
}
