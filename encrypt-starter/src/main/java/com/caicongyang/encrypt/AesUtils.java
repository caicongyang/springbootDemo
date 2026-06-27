package com.caicongyang.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-128 ECB encrypt/decrypt utility.
 * The key is configurable via {@link EncryptProperties}.
 */
public final class AesUtils {

    private static final String ALGORITHM = "AES";

    /** Thread-safe holder for the secret key, set by auto-configuration. */
    private static volatile String secretKey = "caicongyang-2026";

    private AesUtils() {
    }

    public static void setSecretKey(String key) {
        secretKey = key;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    /**
     * Encrypt plaintext to a Base64-encoded cipher string.
     */
    public static String encrypt(String plainText, String secretKey) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            byte[] keyBytes = padKey(secretKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt failed", e);
        }
    }

    /**
     * Decrypt a Base64-encoded cipher string back to plaintext.
     */
    public static String decrypt(String cipherText, String secretKey) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            byte[] keyBytes = padKey(secretKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt failed", e);
        }
    }

    /**
     * Pad or truncate the key to exactly 16 bytes (AES-128).
     */
    private static byte[] padKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] padded = new byte[16];
        int len = Math.min(keyBytes.length, 16);
        System.arraycopy(keyBytes, 0, padded, 0, len);
        return padded;
    }
}
