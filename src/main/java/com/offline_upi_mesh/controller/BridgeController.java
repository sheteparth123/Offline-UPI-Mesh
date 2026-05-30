package com.offline_upi_mesh.controller;

import com.offline_upi_mesh.crypto.HybridCryptoService;
import com.offline_upi_mesh.crypto.PacketHasher;
import com.offline_upi_mesh.dto.EncryptedPacketDto;
import com.offline_upi_mesh.dto.PaymentPayload;
import com.offline_upi_mesh.dto.PaymentRequest;
import com.offline_upi_mesh.service.IdempotencyService;
import com.offline_upi_mesh.service.SettlementService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/bridge")
@RequiredArgsConstructor
public class BridgeController {


    private final HybridCryptoService cryptoService;

    private final SettlementService settlementService;

    private final PacketHasher packetHasher;

    private final IdempotencyService idempotencyService;



    @PostMapping("/ingest")
    public String ingest(
            @RequestBody EncryptedPacketDto packet
    ) throws Exception {


        // Step 1: create fingerprint before decrypting

        String hash =
                packetHasher.hash(packet);



        // Step 2: duplicate protection

        boolean allowed =
                idempotencyService.claim(hash);


        if(!allowed){

            return "Duplicate packet rejected";
        }



        // Step 3: decrypt

        PaymentPayload payload =
                cryptoService.decrypt(packet);



        // Step 4: replay attack check

        if(
                payload.getSignedAt()
                        .isBefore(
                                LocalDateTime.now()
                                        .minusHours(24)
                        )
        ){

            return "Expired packet rejected";
        }



        PaymentRequest request =
                new PaymentRequest();


        request.setSender(
                payload.getSender()
        );


        request.setReceiver(
                payload.getReceiver()
        );


        request.setAmount(
                payload.getAmount()
        );



        return settlementService
                .settle(request,hash);
    }

}