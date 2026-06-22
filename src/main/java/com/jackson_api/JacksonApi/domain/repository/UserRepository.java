package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAll();

    List<User> findByFirstNameIgnoreCase(String firstName);

    List<User> findByFirstNameContainingIgnoreCase(String name);

    Optional<User> findByEmail(String email);

    List<User> findByRoleName(String role);

    boolean existsByEmail(String email);

    long countByRoleName(String roleName);
}
