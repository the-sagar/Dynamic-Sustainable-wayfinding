package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;

import android.app.Service;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleServices extends Service {
    private PackedDataProcessor packedDataProcessor = new SignatureUtility(Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErPj6Jj9MA/la3U9I5pAesvajPJfYwgGuUzcuLjznJNCuBIy/AIbsZazlj562wCNtMAHWBBoqG43hIVVKPPf0gQ==", android.util.Base64.DEFAULT));

    private final static String TAG = BleServices.class.getSimpleName();

    private BluetoothManager mBluetoothManager;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "ie.tcd.cs7cs3.wayfinding.bluetoothp2p.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "ie.tcd.cs7cs3.wayfinding.bluetoothp2p.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "ie.tcd.cs7cs3.wayfinding.bluetoothp2p.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "ie.tcd.cs7cs3.wayfinding.bluetoothp2p.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "ie.tcd.cs7cs3.wayfinding.bluetoothp2p.EXTRA_DATA";

    public final static UUID UUID_LOCATION_NAVIGATION =
            UUID.fromString(GattProfile.NAVIGATION_SERVICES); //sample-- to be replaced by navigation services UUID

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
        stopAdvertising();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    byte[] data = new byte[1];
    int lastVersion = 0;

    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
                //Remove device from any active subscriptions
                //mRegisteredDevices.remove(device);
            }
        }

        //https://stackoverflow.com/questions/46317971/bluetoothgattservercallback-oncharacteristicreadrequest-called-multiple-time
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {

            if (UUID.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC).equals(characteristic.getUuid())) {
                Log.i(TAG, String.format("Read CurrentTime reqid = %s, offset = %s", requestId, offset));


                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        offset,
                        Arrays.copyOfRange(data, offset, data.length)
                );
            } else {
                // Invalid characteristic
                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
            }
        }
    };

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: " + errorCode);
        }
    };

    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void StartBLE() {
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        // We can't continue without proper Bluetooth support
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            //TODO Show the error
        }

        startAdvertising();
        startServer();
    }

    public void StartBLEServer() {
        StartBLE();
    }

    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }

    private void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(UUID.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC)))
                .build();

        mBluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback);
    }

    private void startServer() {
        mBluetoothGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }
        if (p2pbs==null){
            p2pbs = createP2pService();
        }
        mBluetoothGattServer.addService(p2pbs);
    }

    private void stopServer() {
        mBluetoothGattServer.removeService(p2pbs);
    }
    private BluetoothGattService p2pbs;
    public static BluetoothGattService createP2pService() {
        BluetoothGattService service = new BluetoothGattService(UUID.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);


        BluetoothGattCharacteristic localTime = new BluetoothGattCharacteristic(UUID.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC),
                //Read-only characteristic
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        service.addCharacteristic(localTime);

        return service;
    }

    public class LocalBinder extends Binder {
        public BleServices getService() {
            return BleServices.this;
        }
    }
    BluetoothLeScanner scanner;
    ScanCb scanCb = new ScanCb();
    public void ScanForTarget() {
        scanner = mBluetoothManager.getAdapter().getBluetoothLeScanner();

        AsyncTask.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                ScanFilter sf = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC)).build();
                ArrayList<ScanFilter> sl = new ArrayList<>();
                sl.add(sf);
                scanner.startScan(sl, new ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build(), scanCb);
            }
        });


    }

    int statusw = 0;
    long connectioncooldown = 0;

    class ScanCb extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, result.getDevice().getAddress());
            long currenttime = SystemClock.uptimeMillis();
            if(currenttime + 100000 < connectioncooldown) {
                statusw = 0;
                Log.d(TAG, "Reseted timer");
            }
            if (!(statusw == BluetoothProfile.STATE_CONNECTING || statusw == BluetoothProfile.STATE_CONNECTED)){
                statusw = BluetoothProfile.STATE_CONNECTING;
                BluetoothGatt device = result.getDevice().connectGatt(BleServices.this.getApplicationContext(), false, new ClientCB());
                connectioncooldown = SystemClock.uptimeMillis();
            }

        }
    }

    class ClientCB extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            statusw = newState;
            if (status == 257) {
                Log.i(TAG, "onConnectionStateChange: Forced Reset 257");
                //scanner.stopScan(scanCb);
                //ScanForTarget();
            }
            if (newState == BluetoothProfile.STATE_CONNECTED){
                gatt.discoverServices();
            }
            if(newState == BluetoothProfile.STATE_DISCONNECTED){
                gatt.close();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            java.util.List<android.bluetooth.BluetoothGattService> bs = gatt.getServices();
            for (BluetoothGattService bss:
                 bs) {
                Log.i(TAG, bss.getUuid().toString());
            }

            BluetoothGattService gattService = gatt.getService(UUID.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC));
            if (gattService != null) {
                BluetoothGattCharacteristic gch =  gattService.getCharacteristic(UUID.fromString(GattProfile.NAVIGATION_SERVICES_HINT_CHARACTERISTIC));
                gatt.readCharacteristic(gch);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, characteristic.getValue().toString());
            SetValue(characteristic.getValue());
            gatt.disconnect();

        }
    }

    public void SetValue(byte[] data){
        try {
            PackedData pd = packedDataProcessor.parse(data);
            if (!pd.getCheckVersion()) {
                return;
            }
            if (pd.getReadVersion()> lastVersion){
                lastVersion = pd.getReadVersion();
                this.data = data;
            }
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
    public byte[] GetValue(){
        return this.data;
    }
    public byte[] GetValueContent(){
        try {
            PackedData pd = packedDataProcessor.parse(this.data);
            return pd.getContent();
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
        return null;
    }
}
