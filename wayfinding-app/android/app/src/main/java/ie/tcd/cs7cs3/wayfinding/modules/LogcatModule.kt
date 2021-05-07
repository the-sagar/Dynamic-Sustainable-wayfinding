package ie.tcd.cs7cs3.wayfinding.modules

import android.content.Intent
import com.dp.logcatapp.LogcatActivity
import com.facebook.react.bridge.*

class LogcatModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {

    companion object {
        lateinit var reactContext: ReactApplicationContext
    }

    init {
        reactContext = context
    }

    override fun getName(): String {
        return "LogcatActivity"
    }

    @ReactMethod
    fun startActivity() {
        val context = reactApplicationContext
        val intent = Intent(context, LogcatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }
}
