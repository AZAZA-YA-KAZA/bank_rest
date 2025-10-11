package com.example.bankcards.repository;

import com.example.bankcards.entity.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpa, Integer> {

}
