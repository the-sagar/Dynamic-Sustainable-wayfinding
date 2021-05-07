package com.github.tegola.mobile.controller

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.io.IOException

class GPKG {
    class F_GPKG_BUNDLE_ROOT_DIR private constructor(context: Context) : File(context.getExternalFilesDir(null), Constants.Strings.GPKG_BUNDLE.SUBDIR
    ) {
        companion object {
            private var m_this: F_GPKG_BUNDLE_ROOT_DIR? = null
            @JvmStatic
            @Throws(PackageManager.NameNotFoundException::class, IOException::class)
            fun getInstance(context: Context): F_GPKG_BUNDLE_ROOT_DIR? {
                if (m_this == null) m_this = F_GPKG_BUNDLE_ROOT_DIR(context)
                return m_this
            }
        }
    }
}
