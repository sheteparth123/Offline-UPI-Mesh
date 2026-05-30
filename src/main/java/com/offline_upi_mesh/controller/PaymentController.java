package com.offline_upi_mesh.controller;

import com.offline_upi_mesh.dto.PaymentRequest;
import com.offline_upi_mesh.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final SettlementService settlementService;

    @PostMapping("/pay")
    public String pay(@RequestBody PaymentRequest request) {
        return settlementService.settle(request,null);
    }
}