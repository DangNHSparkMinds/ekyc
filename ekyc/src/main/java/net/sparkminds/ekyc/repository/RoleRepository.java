package net.sparkminds.ekyc.repository;

import net.sparkminds.ekyc.entity.Role;
import net.sparkminds.ekyc.service.dto.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}
