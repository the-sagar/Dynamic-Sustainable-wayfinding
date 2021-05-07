package ie.tcd.cs7cs3.wayfinding.modules

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.mikepenz.aboutlibraries.LibsBuilder
import ie.tcd.cs7cs3.wayfinding.R

class AboutLibsModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {

    companion object {
        lateinit var reactContext: ReactApplicationContext
    }

    init {
        reactContext = context
    }

    override fun getName(): String {
        return "AboutLibsActivity"
    }

    @ReactMethod
    fun startActivity() {
        LibsBuilder()
            .withActivityTitle(reactContext.getString(R.string.open_source_licences))
            .withFields(R.string::class.java.fields)
            .start(reactApplicationContext)
    }
}
