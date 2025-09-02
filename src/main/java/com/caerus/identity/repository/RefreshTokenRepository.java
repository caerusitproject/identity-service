package com.caerus.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caerus.identity.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

}
