package com.perpetmatch.modules.Role;

import com.perpetmatch.Domain.Role;
import com.perpetmatch.Domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query()
    Optional<Role> findByName(RoleName roleName);
}
