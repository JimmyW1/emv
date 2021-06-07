package com.vfi.android.emvkernel.utils;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;
import java.util.TreeSet;

import javax.crypto.Cipher;

public class SecurityUtil {
    private static final String TAG = TAGS.SECURITY;

    public static byte[] signRecover(byte[] dataBytes, byte[] expBytes, byte[] modBytes) {

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

    public static String calculateSha1(String dataHex) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(StringUtil.hexStr2Bytes(dataHex));

            StringBuilder buf = new StringBuilder();
            byte[] digest = md.digest();
            buf.append(StringUtil.byte2HexStr(digest));

            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRandomBytesAndBreakDown(int count) {
        Random random = new Random();
        byte[] randomBytes = new byte[count];
        random.nextBytes(randomBytes);

        String randomStr = StringUtil.byte2HexStr(randomBytes);
        LogUtil.d(TAG, "randomBytes=" + randomStr);

        return randomStr.toUpperCase();
    }
}
