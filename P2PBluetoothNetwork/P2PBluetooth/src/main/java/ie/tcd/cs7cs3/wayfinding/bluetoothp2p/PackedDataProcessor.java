package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public interface PackedDataProcessor {
    PackedData parse(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, ClassNotFoundException;
}

