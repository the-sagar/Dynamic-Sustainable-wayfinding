package ie.tcd.cs7cs3.wayfinding.modules

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Base64
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import ie.tcd.cs7cs3.wayfinding.bluetoothp2p.BleServices
import ie.tcd.cs7cs3.wayfinding.bluetoothp2p.PackedDataProcessor
import ie.tcd.cs7cs3.wayfinding.bluetoothp2p.SignatureUtility


class P2PBLEModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {

    companion object {
        lateinit var reactContext: ReactApplicationContext
    }

    init {
        reactContext = context
    }

    override fun getName(): String {
        return "P2PBLEService"
    }

    private var bleServices: BleServices? = null
    private val packedDataProcessor: PackedDataProcessor = SignatureUtility(
        Base64.decode(
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErPj6Jj9MA/la3U9I5pAesvajPJfYwgGuUzcuLjznJNCuBIy/AIbsZazlj562wCNtMAHWBBoqG43hIVVKPPf0gQ==",
            Base64.DEFAULT
        )
    )

    inner class ServConn : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            bleServices = (service as BleServices.LocalBinder).service
            bleServices!!.StartBLEServer()
            bleServices!!.ScanForTarget()
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    @ReactMethod
    fun startService(result: Callback) {
        // Initializes a Bluetooth adapter.  For API level 18 and above, we need to get a reference to BluetoothAdapter through BluetoothManager.
//        val bluetoothManager = reactContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        reactContext.startService(Intent(reactContext, BleServices::class.java))
        val okay: Boolean = reactContext.bindService(
            Intent(reactContext, BleServices::class.java),
            ServConn(),
            Context.BIND_ABOVE_CLIENT or Context.BIND_AUTO_CREATE
        )
        result.invoke(okay)
    }

    @ReactMethod
    fun stopService(result: Callback) {
        result.invoke(reactContext.stopService(Intent(reactContext, BleServices::class.java)))
    }

    @ReactMethod
    fun getIfSupport(result: Callback) {
        result.invoke(reactContext.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
    }

    @ReactMethod
    fun getObject(result: Callback) {
        if(bleServices != null) {
            result.invoke(Base64.encodeToString(bleServices!!.GetValue(), Base64.DEFAULT))
        } else
            result.invoke("")
    }

    @ReactMethod
    fun setValue(signedData: String, result: Callback) {
        if(bleServices != null) {
            bleServices!!.SetValue(Base64.decode(signedData, Base64.DEFAULT))
            result.invoke(true)
        } else
            result.invoke(false)
    }
}
