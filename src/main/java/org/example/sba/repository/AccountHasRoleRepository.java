package org.example.sba.repository;

import org.example.sba.model.AccountHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHasRoleRepository extends JpaRepository<AccountHasRole, Long> {
}

