package com.offline_upi_mesh.dto;


import lombok.Data;


@Data
public class MeshPacket {


    private EncryptedPacketDto packet;


    private int ttl = 5;

}