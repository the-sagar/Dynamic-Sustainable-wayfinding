package ie.tcd.cs7cs3.wayfinding.bluetoothp2p.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import ie.tcd.cs7cs3.wayfinding.bluetoothp2p.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;

/**
 * Activity for scanning available Bluetooth LE devices.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class BleDeviceScanActivity extends AppCompatActivity {

    private PackedDataProcessor packedDataProcessor = new SignatureUtility(Base64.getDecoder().decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErPj6Jj9MA/la3U9I5pAesvajPJfYwgGuUzcuLjznJNCuBIy/AIbsZazlj562wCNtMAHWBBoqG43hIVVKPPf0gQ=="));


    private PackedDataConstructor packedDataConstructor = new SignatureUtility(Base64.getDecoder().decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErPj6Jj9MA/la3U9I5pAesvajPJfYwgGuUzcuLjznJNCuBIy/AIbsZazlj562wCNtMAHWBBoqG43hIVVKPPf0gQ=="), Base64.getDecoder().decode("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCB20fXFGfgrc5Hgex/7VaHpS66ZDlTSF5vyF7CgawkvqA=="));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        BluetoothAdapter mBtAdapter;

        // check to determine whether BLE is supported on the device.  Then we can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, we need to get a reference to BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBtAdapter == null) {
            Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        android.content.ComponentName serviceComp = startService(new Intent(this, BleServices.class));

        boolean okay = bindService(new Intent(this, BleServices.class), new ServConn(), BIND_ABOVE_CLIENT|BIND_AUTO_CREATE);

        ((Button)findViewById(R.id.GetData)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] value = bleServices.GetValue();
                try {
                    PackedData pd = packedDataProcessor.parse(value);
                    ((TextView)findViewById(R.id.textscan)).setText(pd.toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        ((Button)findViewById(R.id.GenerateNewData)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = randomString();
                PackedData pd = new PackedData((int) Instant.now().getEpochSecond(), true, str.getBytes());
                try {
                    byte[] signeddata = packedDataConstructor.parse(pd);
                    bleServices.SetValue(signeddata);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //from https://www.baeldung.com/java-random-string
    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    private BleServices bleServices;

    class ServConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleServices = ((BleServices.LocalBinder) service).getService();
            bleServices.StartBLEServer();
            bleServices.ScanForTarget();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
