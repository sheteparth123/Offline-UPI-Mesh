package com.offline_upi_mesh.service;

import com.offline_upi_mesh.domain.Account;
import com.offline_upi_mesh.domain.TransactionLedger;
import com.offline_upi_mesh.dto.PaymentRequest;
import com.offline_upi_mesh.repository.AccountRepository;
import com.offline_upi_mesh.repository.TransactionLedgerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final AccountRepository accountRepository;
    private final TransactionLedgerRepository ledgerRepository;

    @Transactional
    public String settle(
            PaymentRequest request,
            String packetHash
    ) {

        Account sender = accountRepository
                .findByUsername(request.getSender())
                .orElseThrow(() ->
                        new RuntimeException("Sender not found"));

        Account receiver = accountRepository
                .findByUsername(request.getReceiver())
                .orElseThrow(() ->
                        new RuntimeException("Receiver not found"));

        if(sender.getBalance() < request.getAmount()){


            TransactionLedger ledger =
                    new TransactionLedger();


            ledger.setSender(
                    sender.getUsername()
            );


            ledger.setReceiver(
                    receiver.getUsername()
            );


            ledger.setAmount(
                    request.getAmount()
            );


            ledger.setStatus(
                    "FAILED"
            );


            ledger.setFailureReason(
                    "INSUFFICIENT_BALANCE"
            );


            ledger.setPacketHash(
                    packetHash
            );


            ledger.setCreatedAt(
                    LocalDateTime.now()
            );


            ledgerRepository.save(ledger);


            return "Insufficient Balance";
        }

        sender.setBalance(
                sender.getBalance() - request.getAmount()
        );

        receiver.setBalance(
                receiver.getBalance() + request.getAmount()
        );

        accountRepository.save(sender);
        accountRepository.save(receiver);

        TransactionLedger ledger = new TransactionLedger();

        ledger.setSender(sender.getUsername());
        ledger.setReceiver(receiver.getUsername());
        ledger.setAmount(request.getAmount());
        ledger.setStatus("SUCCESS");
        ledger.setCreatedAt(LocalDateTime.now());
        ledger.setPacketHash(packetHash);

        ledgerRepository.save(ledger);

        return "Payment Successful";
    }
}