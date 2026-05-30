package com.offline_upi_mesh.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offline_upi_mesh.dto.EncryptedPacketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.HexFormat;


@Service
@RequiredArgsConstructor
public class PacketHasher {


    private final ObjectMapper objectMapper;


    public String hash(
            EncryptedPacketDto packet
    ) throws Exception {


        String json =
                objectMapper.writeValueAsString(packet);


        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");


        byte[] hash =
                digest.digest(
                        json.getBytes()
                );


        return HexFormat
                .of()
                .formatHex(hash);
    }
}