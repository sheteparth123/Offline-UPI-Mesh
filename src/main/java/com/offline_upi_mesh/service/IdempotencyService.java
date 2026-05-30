package com.offline_upi_mesh.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;


@Service
public class IdempotencyService {


    private final ConcurrentHashMap<String, Boolean> processedPackets
            = new ConcurrentHashMap<>();


    public boolean claim(
            String hash
    ){

        return processedPackets
                .putIfAbsent(hash,true)
                == null;
    }
}