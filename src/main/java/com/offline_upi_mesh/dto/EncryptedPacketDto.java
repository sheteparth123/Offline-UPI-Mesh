package com.offline_upi_mesh.dto;

import lombok.Data;

@Data
public class EncryptedPacketDto {

    private String encryptedAesKey;

    private String iv;

    private String cipherText;
}