package com.example.bankcards.repository;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.AccountJpa;
import com.example.bankcards.util.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountJpa, Long> {
    @Query("SELECT COUNT(ac) FROM AccountJpa as ac WHERE userId = :userId")
    Boolean existsByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserJpa AS u " +
            "WHERE username = :username")
    Optional<UserDTO> findByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE AccountJpa ac SET ac.balance = :balance WHERE ac.userId = :userId")
    void updateById(@Param("userId") Long userId,
                    @Param("balance") Long balance);

    @Query("SELECT COUNT(ac) FROM AccountJpa as ac WHERE accountNumber = :accountNumber")
    boolean existsByAccountNumber(String accountNumber);

    @Modifying
    @Query("DELETE FROM AccountJpa as ac WHERE ac.userId = :userId")
    void deleteById(@Param("userId") Long userId);
}
