package com.example.bankcards.repository;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpa, Integer> {
    @Query("SELECT COUNT(user) FROM user WHERE username = :username")
    Boolean existsByUsername(@Param("username") String useername);

    @Query("SELECT user FROM user " +
            "WHERE username = :username")
    Optional<UserDTO> findByUsername(@Param("username") String username);


}
