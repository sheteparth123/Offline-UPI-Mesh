package com.offline_upi_mesh.crypto;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class KeyManagerService {


    private KeyPair keyPair;


    @PostConstruct
    public void generateKeys() throws Exception {

        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);

        keyPair = generator.generateKeyPair();

        System.out.println("RSA Keys Generated Successfully");
    }


    public PublicKey getPublicKey(){

        return keyPair.getPublic();
    }


    public PrivateKey getPrivateKey(){

        return keyPair.getPrivate();
    }
}