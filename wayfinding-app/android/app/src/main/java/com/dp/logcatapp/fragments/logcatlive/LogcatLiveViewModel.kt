package com.dp.logcatapp.fragments.logcatlive

import android.app.Application
import com.dp.logcatapp.utils.ScopedAndroidViewModel

internal class LogcatLiveViewModel(application: Application) : ScopedAndroidViewModel(application) {
    var autoScroll = true
    var scrollPosition = 0
}
