package com.kshitij.collegeerp.auth.repository;

import com.kshitij.collegeerp.auth.entity.RefreshToken;
import com.kshitij.collegeerp.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
