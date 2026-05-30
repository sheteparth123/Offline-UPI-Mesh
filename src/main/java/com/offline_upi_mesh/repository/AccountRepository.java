package com.offline_upi_mesh.repository;

import com.offline_upi_mesh.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
   Optional<Account> findByUsername(String username);
}
