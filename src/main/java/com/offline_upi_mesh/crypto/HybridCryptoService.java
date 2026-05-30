package com.offline_upi_mesh.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offline_upi_mesh.dto.EncryptedPacketDto;
import com.offline_upi_mesh.dto.PaymentPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.security.SecureRandom;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class HybridCryptoService {


    private final KeyManagerService keyManagerService;


    private final ObjectMapper objectMapper;


    public EncryptedPacketDto encrypt(
            PaymentPayload payload
    ) throws Exception {


        // 1. Convert payment object to JSON

        String json =
                objectMapper.writeValueAsString(payload);



        // 2. Generate AES-256 key

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance("AES");

        keyGenerator.init(256);

        SecretKey aesKey =
                keyGenerator.generateKey();



        // 3. Create random IV

        byte[] iv = new byte[12];

        SecureRandom random =
                new SecureRandom();

        random.nextBytes(iv);



        // 4. Encrypt payment using AES-GCM

        Cipher aesCipher =
                Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );


        aesCipher.init(
                Cipher.ENCRYPT_MODE,
                aesKey,
                new GCMParameterSpec(
                        128,
                        iv
                )
        );


        byte[] cipherText =
                aesCipher.doFinal(
                        json.getBytes()
                );



        // 5. Encrypt AES key using RSA public key

        Cipher rsaCipher =
                Cipher.getInstance(
                        "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
                );


        rsaCipher.init(
                Cipher.ENCRYPT_MODE,
                keyManagerService.getPublicKey()
        );


        byte[] encryptedAesKey =
                rsaCipher.doFinal(
                        aesKey.getEncoded()
                );



        // 6. Create final packet

        EncryptedPacketDto dto =
                new EncryptedPacketDto();


        dto.setCipherText(
                Base64.getEncoder()
                        .encodeToString(cipherText)
        );


        dto.setIv(
                Base64.getEncoder()
                        .encodeToString(iv)
        );


        dto.setEncryptedAesKey(
                Base64.getEncoder()
                        .encodeToString(encryptedAesKey)
        );


        return dto;
    }

    public PaymentPayload decrypt(
            EncryptedPacketDto packet
    ) throws Exception {


        // 1. Decode encrypted AES key

        byte[] encryptedAesKey =
                Base64.getDecoder()
                        .decode(
                                packet.getEncryptedAesKey()
                        );


        // 2. Decrypt AES key using RSA private key

        Cipher rsaCipher =
                Cipher.getInstance(
                        "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
                );


        rsaCipher.init(
                Cipher.DECRYPT_MODE,
                keyManagerService.getPrivateKey()
        );


        byte[] aesKeyBytes =
                rsaCipher.doFinal(
                        encryptedAesKey
                );


        SecretKey aesKey =
                new SecretKeySpec(
                        aesKeyBytes,
                        "AES"
                );


        // 3. Decode IV

        byte[] iv =
                Base64.getDecoder()
                        .decode(
                                packet.getIv()
                        );


        // 4. Decrypt payment data

        Cipher aesCipher =
                Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );


        aesCipher.init(
                Cipher.DECRYPT_MODE,
                aesKey,
                new GCMParameterSpec(
                        128,
                        iv
                )
        );


        byte[] plainText =
                aesCipher.doFinal(
                        Base64.getDecoder()
                                .decode(packet.getCipherText())
                );


        String json =
                new String(
                        plainText,
                        StandardCharsets.UTF_8
                );


        // JSON back to PaymentPayload

        return objectMapper.readValue(
                json,
                PaymentPayload.class
        );
    }
}