package ie.tcd.cs7cs3.wayfinding.bluetoothp2p.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import ie.tcd.cs7cs3.wayfinding.bluetoothp2p.BleServices;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {Activity BleServices}, which in turn interacts with the
 * Bluetooth LE API.
 */

public class BleDeviceControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);
    }
}
