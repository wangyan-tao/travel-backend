package com.qingchun.travelloan.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

public class SM4Util {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] deriveKey(String username) {
        byte[] src = (username == null ? "" : username).getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = i < src.length ? src[i] : 0;
        }
        return key;
    }

    public static String decryptHexEcb(String cipherHex, byte[] key) {
        try {
            byte[] cipher = hexToBytes(cipherHex);
            Cipher c = Cipher.getInstance("SM4/ECB/PKCS7Padding", "BC");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "SM4"));
            byte[] plain = c.doFinal(cipher);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("SM4 decrypt failed");
        }
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
