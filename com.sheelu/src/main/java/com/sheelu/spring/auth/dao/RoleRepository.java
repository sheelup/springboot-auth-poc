package com.sheelu.spring.auth.dao;

import com.sheelu.spring.auth.models.Role;
import com.sheelu.spring.auth.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role save(Role role);
    Optional<Role> findByName(UserRole role);
}
