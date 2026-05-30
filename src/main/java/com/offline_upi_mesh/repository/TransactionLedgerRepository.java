package com.offline_upi_mesh.repository;

import com.offline_upi_mesh.domain.TransactionLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLedgerRepository
        extends JpaRepository<TransactionLedger, Long> {
    boolean existsByPacketHash(String packetHash);
}