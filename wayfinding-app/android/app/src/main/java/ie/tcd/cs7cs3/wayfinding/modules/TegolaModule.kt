package ie.tcd.cs7cs3.wayfinding.modules

import android.os.Handler
import android.util.Log
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.github.tegola.mobile.controller.ClientAPI
import com.github.tegola.mobile.controller.ClientAPI.ControllerNotificationsListener
import com.github.tegola.mobile.controller.FGS
import com.github.tegola.mobile.controller.GPKG
import ie.tcd.cs7cs3.wayfinding.R
import java.io.*

class TegolaModule(context: ReactApplicationContext) :
        ReactContextBaseJavaModule(context),
        ControllerNotificationsListener {

    companion object {
        lateinit var reactContext: ReactApplicationContext
        lateinit var m_controllerClient: ClientAPI.Client
        val TAG = TegolaModule::class.qualifiedName
    }

    init {
        reactContext = context
    }

    var tegolaPort: Int = 23897
        @ReactMethod set
        @ReactMethod get

    override fun getName(): String {
        return "TegolaWrapper"
    }

    @ReactMethod
    fun queryState() {
        m_controllerClient.mvt_server__query_state__is_running()
    }

    @ReactMethod
    fun initClient() {
        m_controllerClient = ClientAPI.initClient(reactContext, this,
                Handler(reactContext.mainLooper)
        )
        m_controllerClient.controller__start(TegolaModule::class.java.name)
    }

    @ReactMethod
    fun startMVTServer(gpkg_bundle: String, config: String) {
        m_controllerClient.mvt_server__start(
                FGS.MVT_SERVER_START_SPEC__GPKG_PROVIDER(gpkg_bundle, config)
        )
    }

    @ReactMethod
    fun stopMVTServer() {
        m_controllerClient.mvt_server__stop()
    }

    @ReactMethod
    fun writeConfig(gpkg: String) {
//        val parent = reactContext.getExternalFilesDir(null) ?: return
        // gpkg = ireland/ireland.gpkg
        val gpkgPath = File(GPKG.F_GPKG_BUNDLE_ROOT_DIR.getInstance(reactContext), gpkg)
        if (!gpkgPath.exists()) {
            Log.w(TAG, "Unable to write config since gpkg file is not exist: ${gpkgPath.path}")
            return
        }
        // gpkgPath = ...gpkg-bundle/ireland/ireland.gpkg
        val configOut = File(gpkgPath.parent, "config.toml")
        if (!configOut.exists())
            configOut.createNewFile()
        var os = PrintWriter(FileWriter(configOut))
        val template = BufferedReader(InputStreamReader(reactContext.resources
                .openRawResource(R.raw.template_tegola_config)))
        template.lineSequence().forEach {
            os.println(it
                    .replace("%%%LISTEN_PORT%%%", ":$tegolaPort")
                    .replace("%%%CACHE_PATH%%%", reactContext.externalCacheDir?.path
                            ?: reactContext.cacheDir.path)
                    .replace("%%%GPKG_PATH%%%", gpkgPath.path)
            )
        }
        os.flush()
        os.close()
        Log.d(TAG, "Successfully write config to ${configOut.path}")
        val versionOut = File(gpkgPath.parent, "version.properties")
        if (!versionOut.exists())
            versionOut.createNewFile()
        os = PrintWriter(FileWriter(versionOut))
        os.println("TOML_FILE=config.toml")
        os.println("GPKG_FILES=${gpkgPath.name}")
        os.println("GPKG_PATH_ENV_VARS=")
        os.flush()
        os.close()
    }

    override fun OnControllerStarting() {}
    override fun OnControllerRunning() {}
    override fun OnControllerStopping() {}
    override fun OnControllerStopped() {}
    override fun OnMVTServerStarting() {}

    private fun sendEvent(eventName: String, data: Object) {
        reactContext.getJSModule(RCTDeviceEventEmitter::class.java)
                .emit(eventName, data)
    }

    override fun OnMVTServerStartFailed(reason: String) {sendEvent("OnMVTServerStartFailed", reason as Object)}
    override fun OnMVTServerRunning(pid: Int) { sendEvent("OnMVTServerRunning", pid as Object) }
    override fun OnMVTServerListening(port: Int) { sendEvent("OnMVTServerListening", port as Object) }
    override fun OnMVTServerOutputLogcat(logcat_line: String) {}
    override fun OnMVTServerOutputStdErr(stderr_line: String) { sendEvent("OnMVTServerOutputStdErr", stderr_line as Object) }
    override fun OnMVTServerOutputStdOut(stdout_line: String) { sendEvent("OnMVTServerOutputStdOut", stdout_line as Object) }
    override fun OnMVTServerJSONRead(tegola_url_root: String, json_url_endpoint: String, json: String, purpose: String) {}
    override fun OnMVTServerJSONReadFailed(tegola_url_root: String, json_url_endpoint: String, purpose: String, reason: String) {}
    override fun OnMVTServerStopping() {sendEvent("OnMVTServerStopping", 0 as Object)}
    override fun OnMVTServerStopped() {sendEvent("OnMVTServerStopped", 0 as Object)}
}
