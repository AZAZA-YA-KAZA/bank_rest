package com.example.bankcards.repository;

import com.example.bankcards.dto.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Query("DELETE FROM user WHERE username = :username")
    void deleteByUser(@Param("username") String username);

    @Query("SELECT urt FROM RefreshTokenJpa AS urt" +
            "WHERE refreshToken = :refreshToken")
    Optional<RefreshToken> findByToken(@Param("refresh_token") String refreshToken);
}
