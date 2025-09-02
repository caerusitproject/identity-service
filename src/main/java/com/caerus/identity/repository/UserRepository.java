package com.caerus.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caerus.identity.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
