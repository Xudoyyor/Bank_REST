package com.backend.bankcards.service.securityService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private final String algorithm = "AES";
    private final SecretKeySpec keySpec;

    public EncryptionUtil(@Value("${app.encryption.key}") String key) {
        byte[] keyBytes = key.getBytes();
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Kritik xato: 'app.encryption.key' uzunligi 16, 24 yoki 32 belgi bo'lishi kerak!");
        }
        this.keySpec = new SecretKeySpec(keyBytes, algorithm);
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Shifrlashda xatolik: " + e.getMessage());
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Shifrdan yechishda xatolik: " + e.getMessage());
        }
    }
}