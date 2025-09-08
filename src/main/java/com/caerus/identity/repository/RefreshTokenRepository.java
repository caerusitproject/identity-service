package com.caerus.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caerus.identity.entity.RefreshToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUserEmail(String userEmail);

    @Modifying
    int deleteByToken(String token);
}

