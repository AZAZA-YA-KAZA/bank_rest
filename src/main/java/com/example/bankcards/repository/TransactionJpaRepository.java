package com.example.bankcards.repository;

import com.example.bankcards.entity.TransactionJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpa, Integer> {

}
