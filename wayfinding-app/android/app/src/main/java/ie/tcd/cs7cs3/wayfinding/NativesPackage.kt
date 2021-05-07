package ie.tcd.cs7cs3.wayfinding

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager
import ie.tcd.cs7cs3.wayfinding.modules.AboutLibsModule
import ie.tcd.cs7cs3.wayfinding.modules.LogcatModule
import ie.tcd.cs7cs3.wayfinding.modules.P2PBLEModule
import ie.tcd.cs7cs3.wayfinding.modules.TegolaModule
import ie.tcd.cs7cs3.wayfinding.service.routing.RoutingWrapper

class NativesPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext):
            MutableList<NativeModule> {
        return mutableListOf(
            LogcatModule(reactContext),
            TegolaModule(reactContext),
            AboutLibsModule(reactContext),
            RoutingWrapper(reactContext),
            P2PBLEModule(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): MutableList<ViewManager<View, ReactShadowNode<*>>> {
        return mutableListOf()
    }
}
