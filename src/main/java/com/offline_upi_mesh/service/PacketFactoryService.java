package com.offline_upi_mesh.service;

import com.offline_upi_mesh.crypto.HybridCryptoService;
import com.offline_upi_mesh.dto.EncryptedPacketDto;
import com.offline_upi_mesh.dto.PaymentPayload;
import com.offline_upi_mesh.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PacketFactoryService {


    private final HybridCryptoService cryptoService;


    public EncryptedPacketDto createPacket(
            PaymentRequest request
    ) throws Exception {


        PaymentPayload payload =
                new PaymentPayload();


        payload.setSender(
                request.getSender()
        );

        payload.setReceiver(
                request.getReceiver()
        );

        payload.setAmount(
                request.getAmount()
        );


        // unique payment id
        payload.setNonce(
                UUID.randomUUID().toString()
        );


        // payment creation time
        payload.setSignedAt(
                LocalDateTime.now()
        );


        return cryptoService.encrypt(payload);
    }
}