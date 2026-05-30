package com.offline_upi_mesh.service;


import com.offline_upi_mesh.crypto.HybridCryptoService;
import com.offline_upi_mesh.crypto.PacketHasher;
import com.offline_upi_mesh.dto.EncryptedPacketDto;
import com.offline_upi_mesh.dto.PaymentPayload;
import com.offline_upi_mesh.dto.PaymentRequest;

import com.offline_upi_mesh.repository.TransactionLedgerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class BridgeSettlementService {


    private final HybridCryptoService cryptoService;

    private final SettlementService settlementService;

    private final PacketHasher packetHasher;

    private final TransactionLedgerRepository ledgerRepository;



    public String process(
            EncryptedPacketDto packet
    ) throws Exception {


        // SHA-256 duplicate check

        String hash =
                packetHasher.hash(packet);


        if(
                ledgerRepository.existsByPacketHash(hash)
        ){

            return "Duplicate ignored";
        }



        // decrypt

        PaymentPayload payload =
                cryptoService.decrypt(packet);



        // expiry check

        if(
                payload.getSignedAt()
                        .isBefore(
                                LocalDateTime.now()
                                        .minusHours(24)
                        )
        ){

            return "Expired packet";
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