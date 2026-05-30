package com.offline_upi_mesh.controller;


import com.offline_upi_mesh.dto.EncryptedPacketDto;
import com.offline_upi_mesh.dto.PaymentRequest;
import com.offline_upi_mesh.service.MeshNetworkService;
import com.offline_upi_mesh.service.PacketFactoryService;
import com.offline_upi_mesh.domain.VirtualNode;
import com.offline_upi_mesh.repository.VirtualNodeRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mesh")
@RequiredArgsConstructor
public class MeshController {


    private final PacketFactoryService packetFactoryService;

    private final MeshNetworkService meshNetworkService;

    private final VirtualNodeRepository nodeRepository;



    // Only for testing encryption
    @PostMapping("/inject")
    public EncryptedPacketDto inject(
            @RequestBody PaymentRequest request
    ) throws Exception {


        return packetFactoryService
                .createPacket(request);
    }




    // Real offline payment creation
    // Stores packet inside virtual phone

    @PostMapping("/offline-pay/{nodeName}")
    public String offlinePay(
            @PathVariable String nodeName,
            @RequestBody PaymentRequest request
    ) throws Exception {


        return meshNetworkService
                .createOfflinePayment(
                        nodeName,
                        request
                );
    }





    // Phone-to-phone packet transfer

    @PostMapping("/sync")
    public String sync(
            @RequestParam String from,
            @RequestParam String to
    ) throws Exception {


        return meshNetworkService
                .sync(
                        from,
                        to
                );
    }

    @GetMapping("/nodes")
    public List<VirtualNode> getNodes(){

        return nodeRepository.findAll();
    }

    @PostMapping("/gossip")
    public String gossip()
            throws Exception {


        return meshNetworkService
                .autoGossip();
    }

    @PostMapping("/internet/{nodeName}")
    public String updateInternet(
            @PathVariable String nodeName,
            @RequestParam boolean status
    ) throws Exception {


        return meshNetworkService
                .updateInternet(
                        nodeName,
                        status
                );
    }

    @PostMapping("/qr/{nodeName}")
    public String generateQr(
            @PathVariable String nodeName
    ) throws Exception {


        return meshNetworkService
                .generateQr(
                        nodeName
                );
    }

    @PostMapping("/scan/{nodeName}")
    public String scanQr(
            @PathVariable String nodeName,
            @RequestParam String path
    ) throws Exception {

        return meshNetworkService
                .scanQr(
                        nodeName,
                        path
                );
    }


}