package com.example.bankcards.repository;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.CardJpa;
import com.example.bankcards.util.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardJpaRepository extends JpaRepository<CardJpa, Long>,
        JpaSpecificationExecutor<CardJpa> {

    boolean existsByUserUserId(Long userId);
    boolean existsByCardNumber(String cardNumber);

    @Modifying
    @Query("UPDATE CardJpa c SET c.balance = :balance WHERE c.cardId = :cardId")
    void updateBalance(@Param("cardId") Long cardId, @Param("balance") Long balance);

    @Modifying
    @Query("UPDATE CardJpa c SET c.status = :status WHERE c.cardId = :cardId")
    void updateStatus(@Param("cardId") Long cardId, @Param("status") CardStatus status);

    Page<CardJpa> findByUserUserId(Long userId, Pageable pageable);

    List<CardJpa> findByUserUserId(Long userId);

    // Для фильтрации (используем Specification для динамики)
}
