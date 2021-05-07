package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignatureUtility implements PackedDataConstructor, PackedDataProcessor {

    public static final String PRIVATE_KEY = "PRIVATE_KEY";
    public static final String PUBLIC_KEY = "PUBLIC_KEY";
    public static final String keyPath = "keys/";

    private byte[] privateKeyBlob;
    private byte[] publicKeyBlob;

    public SignatureUtility(String publicKeyPath) throws IOException {
        byte[] publicKey = readFileByFilePath(publicKeyPath);

        publicKeyBlob = publicKey;
    }

    public SignatureUtility(String publicKeyPath, String privateKeyPath) {
        try {
            byte[] publicKey = readFileByFilePath(publicKeyPath);
            byte[] privateKey = readFileByFilePath(privateKeyPath);

            privateKeyBlob = privateKey;
            publicKeyBlob = publicKey;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public SignatureUtility(byte[] publicKey){
        publicKeyBlob = publicKey;
    }

    public SignatureUtility(byte[] publicKey, byte[] privateKey){
        privateKeyBlob = privateKey;
        publicKeyBlob = publicKey;
    }

    public SignatureUtility(){
        this(keyPath + "/public.key", keyPath + "/private.key");
    }


    //Signing
    public byte[] parse(PackedData data) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, ClassNotFoundException, InvalidAlgorithmParameterException, InvalidKeySpecException {

        PrivateKey privateKey = (PrivateKey) readKeyFromFile(PRIVATE_KEY);

        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        SignedObject so = new SignedObject(data, privateKey, ecdsaSign);
        //Converting signed object to bytes for sending
        byte[] resolvedData = convertToBytes(so);
        return resolvedData;
    }

    public PackedData parse(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, ClassNotFoundException {
        //Receiver side logic
        SignedObject receivedData = (SignedObject) convertToObject(data);    //Converting received bytes to SignedObject
        PackedData resolvedData = (PackedData) receivedData.getObject();    //Getting the PackedData from SignedObject
        PublicKey publicKey = (PublicKey) readKeyFromFile(PUBLIC_KEY);      //Reading key from file
        if(publicKey!=null){
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            boolean result = receivedData.verify(publicKey, ecdsaSign);        //Verifying the data in the PackedData with public key
            System.out.println("Verified: " + result);
            if(result){
                System.out.println("Data verified successfully !");
                return resolvedData;
            }else{
                return new PackedData(-1, false, "Data verification failed".getBytes());
            }
        }else{
            return new PackedData(-1, false, "Error reading public key".getBytes());
        }
    }

    public Key readKeyFromFile(String keyType) {
        if (keyType.equalsIgnoreCase(PUBLIC_KEY)) {
            try {

                byte[] encodedPublicKey = publicKeyBlob;
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
                return publicKey;
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                System.out.println("Error with algorithm for key !");
                e.printStackTrace();
            }
        } else {
            try {
                byte[] encodedPrivateKey = privateKeyBlob;
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
                return privateKey;
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                System.out.println("Error with algorithm for key !");
                e.printStackTrace();
            }
        }
        return null;
    }

    private byte[] readFileByFilePath(String s) throws IOException {
        File filePublicKey = new File(s);
        FileInputStream fis = new FileInputStream(s);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();
        return encodedPublicKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveKeyToFile(Key keyType) {
        Path path = Paths.get(keyPath);
        if (keyType instanceof PublicKey) {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyType.getEncoded());

            try {
                if (!Files.exists(path)) {
                    Files.createDirectory(path);
                    System.out.println("Directory created");
                }
                FileOutputStream fos = new FileOutputStream(keyPath + "/public.key");
                fos.write(x509EncodedKeySpec.getEncoded());
                fos.close();
            } catch (IOException ex) {
                System.out.println("Could not save public key !");
                ex.printStackTrace();
            }
        } else {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyType.getEncoded());
            try {
                FileOutputStream fos = new FileOutputStream(keyPath + "/private.key");
                fos.write(pkcs8EncodedKeySpec.getEncoded());
                fos.close();
            } catch (IOException ex) {
                System.out.println("Could not save private key !");
                ex.printStackTrace();
            }
        }
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

}

