package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public interface PackedDataConstructor {
    byte[] parse(PackedData data) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, ClassNotFoundException, InvalidAlgorithmParameterException, InvalidKeySpecException;
}
