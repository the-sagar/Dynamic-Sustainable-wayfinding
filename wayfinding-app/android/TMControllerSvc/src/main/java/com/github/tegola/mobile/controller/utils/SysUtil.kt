package com.github.tegola.mobile.controller.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Process
import android.system.Os
import android.util.Log

class SysUtil {
    companion object {
        fun LD_LIBRARY_PATH(context: Context): String {
            val myLibPath = context.getApplicationInfo().nativeLibraryDir
            val systemLibPath = if (is64Bit()) "/vendor/lib64:/system/lib64" else "/vendor/lib:/system/lib"
            return "$myLibPath:$systemLibPath"
        }

        fun is64Bit(): Boolean {
            var is64bit = false
            if (Build.VERSION.SDK_INT >= 23) {
                is64bit = MarshmallowSysdeps.is64Bit()
            } else if (Build.VERSION.SDK_INT >= 21) {
                try {
                    is64bit = LollipopSysdeps.is64Bit()
                } catch (var2: Exception) {
                    Log.e("SysUtil", String.format("Could not read /proc/self/exe. Err msg: %s", var2.message))
                }
            }
            return is64bit
        }

        @TargetApi(Build.VERSION_CODES.M)
        @DoNotOptimize
        private object MarshmallowSysdeps {
            @DoNotOptimize
            fun is64Bit(): Boolean {
                return Process.is64Bit()
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @DoNotOptimize
        private object LollipopSysdeps {
            @DoNotOptimize
            fun is64Bit(): Boolean {
                return Os.readlink("/proc/self/exe").contains("64")
            }
        }
    }
}

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CONSTRUCTOR)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class DoNotOptimize
