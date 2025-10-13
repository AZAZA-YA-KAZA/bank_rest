package com.example.bankcards.repository;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.util.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpa, Long> {
    @Query("SELECT COUNT(u) FROM UserJpa as u WHERE username = :username")
    Boolean existsByUsername(@Param("username") String useername);

    @Query("SELECT u FROM UserJpa AS u " +
            "WHERE username = :username")
    Optional<UserDTO> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) FROM UserJpa as u WHERE email = :email")
    Boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM UserJpa as u WHERE telephone = :telephone")
    Boolean existsByTelephone(@Param("telephone") String telephone);

    @Modifying
    @Query("UPDATE UserJpa u SET u.username = :username, u.firstName = :firstName, u.surName = :surName, " +
            "u.patronymic = :patronymic, u.email = :email, u.telephone = :telephone, " +
            "u.password = :password, u.role = :role WHERE u.userId = :userId")
    void updateById(@Param("userId") Long userId,
                   @Param("username") String username,
                   @Param("firstName") String firstName,
                   @Param("surName") String surName,
                   @Param("patronymic") String patronymic,
                   @Param("email") String email,
                   @Param("telephone") String telephone,
                   @Param("password") String password,
                   @Param("role") UserRole role);
}
