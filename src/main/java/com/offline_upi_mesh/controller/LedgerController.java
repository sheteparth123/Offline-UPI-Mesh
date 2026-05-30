package com.offline_upi_mesh.controller;

import com.offline_upi_mesh.domain.TransactionLedger;
import com.offline_upi_mesh.repository.TransactionLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final TransactionLedgerRepository ledgerRepository;

    @GetMapping
    public List<TransactionLedger> getAllTransactions() {
        return ledgerRepository.findAll();
    }
}