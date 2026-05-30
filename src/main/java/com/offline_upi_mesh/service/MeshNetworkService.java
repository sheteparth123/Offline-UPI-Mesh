
package com.offline_upi_mesh.service;
import com.offline_upi_mesh.dto.MeshPacket;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.offline_upi_mesh.domain.VirtualNode;
import com.offline_upi_mesh.dto.EncryptedPacketDto;
import com.offline_upi_mesh.repository.VirtualNodeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MeshNetworkService {



    private final VirtualNodeRepository nodeRepository;

    private final PacketFactoryService packetFactoryService;

    private final ObjectMapper objectMapper;

    private final BridgeSettlementService bridgeSettlementService;

    private final QRService qrService;



    public String createOfflinePayment(
            String nodeName,
            com.offline_upi_mesh.dto.PaymentRequest request
    ) throws Exception {



        // find phone

        VirtualNode node =
                nodeRepository
                        .findByNodeName(nodeName)
                        .orElseThrow();



        // create encrypted packet

        EncryptedPacketDto packet =
                packetFactoryService
                        .createPacket(request);

        MeshPacket meshPacket =
                new MeshPacket();


        meshPacket.setPacket(
                packet
        );


        meshPacket.setTtl(5);



        // convert packet to string

        String packetJson =
                objectMapper.writeValueAsString(meshPacket);



        // store inside phone

        node.getStoredPackets()
                .add(packetJson);



        nodeRepository.save(node);



        return "Packet stored in "
                + nodeName;
    }



    public String sync(
            String from,
            String to
    ) throws Exception {


        VirtualNode source =
                nodeRepository
                        .findByNodeName(from)
                        .orElseThrow();


        VirtualNode target =
                nodeRepository
                        .findByNodeName(to)
                        .orElseThrow();



        // copy packets from one phone to another

        for(String packetJson :
                source.getStoredPackets()
        ){


            MeshPacket meshPacket =
                    objectMapper.readValue(
                            packetJson,
                            MeshPacket.class
                    );


            // reduce hop count

            meshPacket.setTtl(
                    meshPacket.getTtl() - 1
            );



            if(meshPacket.getTtl() > 0){


                String updatedPacket =
                        objectMapper
                                .writeValueAsString(
                                        meshPacket
                                );


                if(
                        !target.getStoredPackets()
                                .contains(updatedPacket)
                ){

                    target.getStoredPackets()
                            .add(updatedPacket);
                }

            }

        }


        nodeRepository.save(target);



        // if receiver phone has internet

        if(target.isInternetAvailable()){


            for(String packetJson :
                    target.getStoredPackets()
            ){


                MeshPacket meshPacket =
                        objectMapper.readValue(
                                packetJson,
                                MeshPacket.class
                        );


                EncryptedPacketDto packet =
                        meshPacket.getPacket();


                String result =
                        bridgeSettlementService
                                .process(packet);



                System.out.println(
                        "Bridge upload result: "
                                + result
                );
            }



            // remove packets after upload attempt

            target.getStoredPackets()
                    .clear();


            nodeRepository.save(target);
        }



        return "Synced "
                + from
                + " -> "
                + to;
    }
    public String autoGossip() throws Exception {


        var nodes =
                nodeRepository.findAll();


        int transfers = 0;


        for(
                VirtualNode source : nodes
        ){


            if(
                    source.getStoredPackets()
                            .isEmpty()
            ){

                continue;
            }



            for(
                    VirtualNode target : nodes
            ){


                if(
                        source.getId()
                                .equals(target.getId())
                ){

                    continue;
                }



                // reuse our sync logic

                sync(
                        source.getNodeName(),
                        target.getNodeName()
                );


                transfers++;

            }

        }


        return "Gossip completed. Transfers: "
                + transfers;
    }
    public String updateInternet(
            String nodeName,
            boolean status
    ) throws Exception {


        VirtualNode node =
                nodeRepository
                        .findByNodeName(nodeName)
                        .orElseThrow();


        node.setInternetAvailable(status);


        nodeRepository.save(node);



        if(status){


            for(String packetJson :
                    node.getStoredPackets()
            ){


                MeshPacket meshPacket =
                        objectMapper.readValue(
                                packetJson,
                                MeshPacket.class
                        );


                String result =
                        bridgeSettlementService
                                .process(
                                        meshPacket.getPacket()
                                );


                System.out.println(
                        "Internet restored upload: "
                                + result
                );
            }


            node.getStoredPackets()
                    .clear();


            nodeRepository.save(node);
        }


        return nodeName
                + " internet = "
                + status;
    }
    public String generateQr(
            String nodeName
    ) throws Exception {


        VirtualNode node =
                nodeRepository
                        .findByNodeName(nodeName)
                        .orElseThrow();


        if(
                node.getStoredPackets()
                        .isEmpty()
        ){
            return "No packets found";
        }


        String packetJson =
                node.getStoredPackets()
                        .get(
                                node.getStoredPackets()
                                        .size() - 1
                        );


        return qrService.generateQr(
                packetJson,
                nodeName + "_payment"
        );
    }
    public String scanQr(
            String nodeName,
            String qrPath
    ) throws Exception {

        VirtualNode node =
                nodeRepository
                        .findByNodeName(nodeName)
                        .orElseThrow();

        String packetJson =
                qrService.readQr(qrPath);

        node.getStoredPackets()
                .add(packetJson);

        nodeRepository.save(node);

        return "QR scanned into "
                + nodeName;
    }
}