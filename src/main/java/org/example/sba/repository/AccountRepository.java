package org.example.sba.repository;

import org.example.sba.model.Account;
import org.example.sba.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByPhone(String phone);

    @Query("SELECT ar.role FROM AccountHasRole ar WHERE ar.account.id = :accountId")
    List<Role> findRolesByAccountId(@Param("accountId") Long accountId);
}
