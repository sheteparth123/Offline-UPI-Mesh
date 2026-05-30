package com.offline_upi_mesh.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private String sender;
    private String receiver;
    private Double amount;
}