package com.offline_upi_mesh.controller;

import com.offline_upi_mesh.domain.Account;
import com.offline_upi_mesh.repository.AccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/api/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}