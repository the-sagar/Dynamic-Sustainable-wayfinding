package com.github.tegola.mobile.controller;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTests_Ctrlr_Basic {
    private final String TAG = InstrumentedTests_Ctrlr_Basic.class.getCanonicalName();
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Log.d(TAG, "useAppContext: appContext.getPackageName()==\"" + appContext.getPackageName() + "\"");
        assertEquals("com.github.tegola.mobile.controller.test", appContext.getPackageName());
    }
}
