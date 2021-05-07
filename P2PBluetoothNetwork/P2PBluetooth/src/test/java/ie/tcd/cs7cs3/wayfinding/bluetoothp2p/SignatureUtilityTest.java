package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;


import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.assertEquals;

public class SignatureUtilityTest{

    public static final String PRIVATE_KEY = "PRIVATE_KEY";
    public static final String PUBLIC_KEY = "PUBLIC_KEY";
    public static final String keyPath = "keys/";

    @Test
    public void convertToByteAndConvetToObjectTest() {
        try {
            SignatureUtility utility = new SignatureUtility();
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
            KeyPairGenerator g = initializeKey();
            g.initialize(ecSpec, new SecureRandom());
            KeyPair keypair = getKeyPair(g);
            PublicKey publicKey = getPublicKey(keypair);
            PrivateKey privateKey = getPrivateKey(keypair);

            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            PackedData packedData = new PackedData(1, false, "Hello World".getBytes());
            SignedObject testSignedObject = new SignedObject(packedData, privateKey, ecdsaSign);

            //Convertings SignedObject to byte[]
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(testSignedObject);
            out.flush();
            byte[] testData = bos.toByteArray();

            //Calling actual function to get byte[]
            byte[] actualData = utility.convertToBytes(createObject());

            //Converting byte[] to SignedObject
            ByteArrayInputStream bis = new ByteArrayInputStream(testData);
            ObjectInput in = null;
            in = new ObjectInputStream(bis);
            SignedObject o = (SignedObject) in.readObject();

            //Calling actual function to get SignedObject
            SignedObject actualSignedObject = (SignedObject) utility.convertToObject(actualData);

            //Comparing both objects
            equals(actualSignedObject.equals(o));
        }
        catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SignatureUtility utility = new SignatureUtility();
        File filePublicKey = new File(keyPath + "/public.key");
        FileInputStream fis = new FileInputStream(keyPath + "/public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        Key publicKeyActual = utility.readKeyFromFile("PUBLIC_KEY");
        assertEquals(publicKeyActual, publicKey);
    }

    @Test
    public void readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SignatureUtility utility = new SignatureUtility();
        File filePrivateKey = new File(keyPath + "/private.key");
        FileInputStream fis = new FileInputStream(keyPath + "/private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        Key privateKeyActual = utility.readKeyFromFile("PRIVATE_KEY");
        assertEquals(privateKeyActual, privateKey);
    }

    @Test
    public void verifySignTrue() throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, SignatureException, IOException, ClassNotFoundException {
        SignatureUtility utility = new SignatureUtility();
        SignedObject receivedData = createObject();    //Converting received bytes to SignedObject
        PackedData resolvedData = (PackedData) receivedData.getObject();    //Getting the PackedData from SignedObject

        PublicKey publicKey = (PublicKey) utility.readKeyFromFile(PUBLIC_KEY);      //Reading key from file
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        boolean result = receivedData.verify(publicKey, ecdsaSign);        //Verifying the data in the PackedData with public key

        assertEquals(result, true);

        PackedData actualData = utility.parse(convertToBytes(receivedData));

        assertEquals(actualData.getCheckVersion(), resolvedData.getCheckVersion());
    }

    @Test
    public void verifySignFalse() throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, SignatureException, IOException, ClassNotFoundException {
        SignatureUtility utility = new SignatureUtility();

        KeyPairGenerator g = initializeKey();
        KeyPair keypair = getKeyPair(g);
        PrivateKey privateKey = getPrivateKey(keypair);
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        PackedData testData = new PackedData(1, true, "Hello World".getBytes());
        SignedObject testSigned = new SignedObject(testData, privateKey, ecdsaSign);

        PublicKey publicKey = (PublicKey) utility.readKeyFromFile(PUBLIC_KEY);      //Reading key from file
        boolean result = testSigned.verify(publicKey, ecdsaSign);        //Verifying the data in the PackedData with public key

        assertEquals(result, false);

        PackedData actualData = utility.parse(convertToBytes(testSigned));

        assertEquals(false,actualData.getCheckVersion() );
    }

    // Test-case for saving keys
    @Test
    public void saveKeyToFileTest() {
        try {
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
            KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
            g.initialize(ecSpec, new SecureRandom());
            KeyPair keypair = g.generateKeyPair();
            PublicKey publicKey = keypair.getPublic();
            PrivateKey privateKey = keypair.getPrivate();

            SignatureUtility utility = new SignatureUtility();

            utility.saveKeyToFile(publicKey);
            File publicKeyfile = new File(SignatureUtility.keyPath  + "/public.key");
            // Assert that the file is not empty
            Assert.assertTrue(publicKeyfile.length() > 0);

            utility.saveKeyToFile(privateKey);
            File privateKeyfile = new File(SignatureUtility.keyPath  + "/private.key");
            // Assert that the file is not empty
            Assert.assertTrue(privateKeyfile.length() > 0);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    // Test-case for signing the packed data
    @Test
    public void signPackedDataTest() {
        try {
            SignatureUtility utility = new SignatureUtility();

            PackedData data = new PackedData(1, true, "Data to send".getBytes(StandardCharsets.UTF_8));
            byte[] dataToSend = utility.parse(data);
            SignedObject signedData = (SignedObject) utility.convertToObject(dataToSend);
            PublicKey publicKey = (PublicKey) utility.readKeyFromFile(utility.PUBLIC_KEY);
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            boolean result = signedData.verify(publicKey, ecdsaSign);
            System.out.println("Result       : "+result);
            Assert.assertTrue(result);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IOException | SignatureException | ClassNotFoundException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    //UTILITY FUNCTIONS

    public SignedObject createObject() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException, IOException {
        SignatureUtility utility = new SignatureUtility();
        KeyPairGenerator g = initializeKey();
        KeyPair keypair = getKeyPair(g);
        PrivateKey privateKey = (PrivateKey) utility.readKeyFromFile("PRIVATE");
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        PackedData packedData = new PackedData(1, false, "Hello World".getBytes());
        SignedObject so = new SignedObject(packedData, privateKey, ecdsaSign);
        return so;
    }

    //Converting PackedData to byte array
    public byte[] convertToBytes(SignedObject data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(data);
            out.flush();
            byte[] yourBytes = bos.toByteArray();
            return yourBytes;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    //Converting byte array to Object
    public Object convertToObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public KeyPairGenerator initializeKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        return g;
    }

    public KeyPair getKeyPair(KeyPairGenerator g) {
        return g.generateKeyPair();
    }
    public PublicKey getPublicKey(KeyPair kp) {
        return kp.getPublic();
    }

    public PrivateKey getPrivateKey(KeyPair kp) {
        return kp.getPrivate();
    }
}
