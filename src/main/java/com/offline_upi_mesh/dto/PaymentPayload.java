package com.offline_upi_mesh.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentPayload {

    private String sender;

    private String receiver;

    private Double amount;

    private String nonce;

    private LocalDateTime signedAt;
}