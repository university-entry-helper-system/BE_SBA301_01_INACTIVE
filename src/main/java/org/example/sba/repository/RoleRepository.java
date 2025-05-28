package org.example.sba.repository;

import org.example.sba.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);

    @Query(value = "SELECT r FROM Role r inner join AccountHasRole ur on r.id = ur.account.id where ur.account.id =: accountId")
    List<Role> getAllByAccountId(Long accountId);
}
