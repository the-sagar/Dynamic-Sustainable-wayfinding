package com.github.tegola.mobile.controller

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.github.tegola.mobile.controller.Exceptions.TegolaBinaryNotExecutableException
import com.github.tegola.mobile.controller.Exceptions.UnsupportedCPUABIException
import com.github.tegola.mobile.controller.utils.Files
import java.io.File
import java.io.IOException

class TEGOLA_BIN {
    private val TAG = TEGOLA_BIN::class.qualifiedName
    var f_tegola_bin: File
        private set
    var f_tegola_version: String = "unknown"

    private constructor(context: Context) {
        val f_libs: File = Files.F_LIBS_DIR.getInstance(context)
        val s_tegola_bin_for_abi = "libtegola.so"
        if (s_tegola_bin_for_abi != null) {
            f_tegola_bin = File(f_libs, s_tegola_bin_for_abi)
            f_tegola_bin.setReadOnly()
            f_tegola_bin.setExecutable(true)
            if (f_tegola_bin.setExecutable(true)) throw TegolaBinaryNotExecutableException(f_tegola_bin.name)
            Log.d(TAG, "TEGOLA_BIN ctor: teggola bin " + f_tegola_bin.canonicalPath + " " + if (f_tegola_bin.exists()) "exists" else "does NOT exist")
        } else {
            throw UnsupportedCPUABIException("no tegola binary exists for ABI " + Build.CPU_ABI)
        }
    }

    companion object {
        private var m_this: TEGOLA_BIN? = null
        @JvmStatic
        @Throws(PackageManager.NameNotFoundException::class, IOException::class, TegolaBinaryNotExecutableException::class, UnsupportedCPUABIException::class)
        fun getInstance(context: Context): TEGOLA_BIN? {
            if (m_this == null) m_this = TEGOLA_BIN(context)
            return m_this
        }
    }
}
