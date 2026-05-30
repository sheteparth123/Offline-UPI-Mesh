package com.offline_upi_mesh.domain;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TransactionLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String sender;

    private String receiver;

    private Double amount;


    private String status;


    private LocalDateTime createdAt;

    private String failureReason;


    @Column(unique = true)
    private String packetHash;
}