package com.offline_upi_mesh.config;


import com.offline_upi_mesh.domain.Account;
import com.offline_upi_mesh.domain.VirtualNode;
import com.offline_upi_mesh.repository.AccountRepository;
import com.offline_upi_mesh.repository.VirtualNodeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {


    private final AccountRepository accountRepository;

    private final VirtualNodeRepository nodeRepository;


    @Override
    public void run(String... args) {

        if (accountRepository.count() == 0) {

            Account alice = new Account();

            alice.setUsername("alice");
            alice.setBalance(5000.0);

            Account bob = new Account();

            bob.setUsername("bob");
            bob.setBalance(2000.0);

            accountRepository.save(alice);
            accountRepository.save(bob);

            System.out.println(
                    "Accounts seeded"
            );
        }


        if (nodeRepository.count() == 0) {

            VirtualNode alicePhone =
                    new VirtualNode();

            alicePhone.setNodeName("AlicePhone");
            alicePhone.setInternetAvailable(false);


            VirtualNode bobPhone =
                    new VirtualNode();

            bobPhone.setNodeName("BobPhone");
            bobPhone.setInternetAvailable(false);


            VirtualNode bridge =
                    new VirtualNode();

            bridge.setNodeName("BridgePhone");
            bridge.setInternetAvailable(true);


            nodeRepository.save(alicePhone);
            nodeRepository.save(bobPhone);
            nodeRepository.save(bridge);

            System.out.println(
                    "Nodes seeded"
            );
        }
    }
}