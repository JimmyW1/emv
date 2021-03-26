package com.vfi.android.emvkernel.utils;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.TreeSet;

import javax.crypto.Cipher;

public class SecurityUtil {
    private static final String TAG = TAGS.SECURITY;

    public static String RSAVerify(String signedDataHexStr, String modulusHexStr) throws Exception {
        Signature signature = Signature.getInstance("NONEwithRSA");
        BigInteger bigIntModulus = new BigInteger(modulusHexStr,16);
        BigInteger bigIntPrivateExponent = new BigInteger("010001",16);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        signature.initVerify(pubKey);

        return null;
    }

    public static byte[] rsaEncrypt(byte[] data, String modulusHexStr) throws Exception{
        BigInteger bigIntModulus = new BigInteger(modulusHexStr,16);
        BigInteger bigIntPrivateExponent = new BigInteger("010001",16);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] encryptedData = cipher.doFinal(data);

        LogUtil.d(TAG, "encryptedData=[" + StringUtil.byte2HexStr(encryptedData) + "]");
        return encryptedData;
    }

    public static byte[] rsaDecrypt(byte[] data, byte[] privateKey) throws Exception{
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] decryptedData = cipher.doFinal(data);

        LogUtil.d(TAG, "decryptedData=[" + StringUtil.byte2HexStr(decryptedData) + "]");
        return decryptedData;
    }

    public static byte[] rsaDecrypt(String signedDataHexStr, String modulusHexStr) throws Exception{
        BigInteger bigIntModulus = new BigInteger(modulusHexStr,16);
        BigInteger bigIntPrivateExponent = new BigInteger("010001",16);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        byte[] decryptedData = cipher.doFinal(StringUtil.hexStr2Bytes(signedDataHexStr));

        LogUtil.d(TAG, "decryptedData=[" + StringUtil.byte2HexStr(decryptedData) + "]");
        return decryptedData;
    }

    public static byte[] signVerify(byte[] dataBytes, byte[] expBytes, byte[] modBytes) {

        int inBytesLength = dataBytes.length;

        if (expBytes[0] >= (byte) 0x80) {
            //Prepend 0x00 to modulus
            byte[] tmp = new byte[expBytes.length + 1];
            tmp[0] = (byte) 0x00;
            System.arraycopy(expBytes, 0, tmp, 1, expBytes.length);
            expBytes = tmp;
        }

        if (modBytes[0] >= (byte) 0x80) {
            //Prepend 0x00 to modulus
            byte[] tmp = new byte[modBytes.length + 1];
            tmp[0] = (byte) 0x00;
            System.arraycopy(modBytes, 0, tmp, 1, modBytes.length);
            modBytes = tmp;
        }

        if (dataBytes[0] >= (byte) 0x80) {
            //Prepend 0x00 to signed data to avoid that the most significant bit is interpreted as the "signed" bit
            byte[] tmp = new byte[dataBytes.length + 1];
            tmp[0] = (byte) 0x00;
            System.arraycopy(dataBytes, 0, tmp, 1, dataBytes.length);
            dataBytes = tmp;
        }

        BigInteger exp = new BigInteger(expBytes);
        BigInteger mod = new BigInteger(modBytes);
        BigInteger data = new BigInteger(dataBytes);

        byte[] result = data.modPow(exp, mod).toByteArray();

        if (result.length == (inBytesLength+1) && result[0] == (byte)0x00) {
            //Remove 0x00 from beginning of array
            byte[] tmp = new byte[inBytesLength];
            System.arraycopy(result, 1, tmp, 0, inBytesLength);
            result = tmp;
        }

        LogUtil.d(TAGS.SECURITY, "recoveryDatHex=" + StringUtil.byte2HexStr(result));
        return result;
    }

    public static String signVerify(String signedDataHexStr, String modulusHexStr) throws Exception{
        TreeSet<String> algorithms = new TreeSet<>();
        for (Provider provider : Security.getProviders()) {
            System.out.println("=============");
            System.out.println(provider.getName());
            System.out.println("=============");
            for (Provider.Service service : provider.getServices())
                if (service.getType().equals("Signature"))
                    algorithms.add(service.getAlgorithm());
        }
        for (String algorithm : algorithms) {
            System.out.println(algorithm);

            try {
                Signature signatureEngine = Signature.getInstance(algorithm);
//        Signature signatureEngine = Signature.getInstance("SHA1/RSA-ISO9796-2", "IAIK");
//        Signature signatureEngine = Signature.getInstance("SHA1/RSA-ISO9796-2", "IAIK");
                BigInteger bigIntModulus = new BigInteger(modulusHexStr, 16);
                BigInteger bigIntPrivateExponent = new BigInteger("010001", 16);
                RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
                RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
                signatureEngine.initVerify(publicKey);
                boolean ok = signatureEngine.verify(StringUtil.hexStr2Bytes(signedDataHexStr));
                LogUtil.d(TAG, "ok=" + ok);
            } catch (Exception e) {

            }
        }
        return null;
//        AlgorithmParameters recoveredMessage =
//                (AlgorithmParameters)signatureEngine.getParameters();
//        byte[] message = recoveredMessage.getEncoded();
//
//        return StringUtil.byte2HexStr(message);
    }
}
