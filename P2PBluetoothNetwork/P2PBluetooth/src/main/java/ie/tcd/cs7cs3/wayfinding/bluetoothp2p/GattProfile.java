package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes-- Sample UUID to be replaced by Navigation Services
 */

public class GattProfile {

    private static HashMap<String, String> attributes = new HashMap();
    public static String NAVIGATION_SERVICES = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String NAVIGATION_SERVICES_HINT_CHARACTERISTIC = "5601d3ee-a042-48a6-abb5-a03fabd525c7";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Navigation Services");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(NAVIGATION_SERVICES, "Navigation Services");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

}
