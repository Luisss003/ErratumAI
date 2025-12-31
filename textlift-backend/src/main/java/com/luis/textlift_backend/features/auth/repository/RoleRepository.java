package com.luis.textlift_backend.features.auth.repository;

import com.luis.textlift_backend.features.auth.domain.Role;
import com.luis.textlift_backend.features.auth.domain.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleEnum name);
}
