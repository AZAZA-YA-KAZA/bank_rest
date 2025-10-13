package com.example.bankcards.service;

import com.example.bankcards.dto.AccountDTO;
import com.example.bankcards.entity.AccountJpa;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.repository.AccountJpaRepository;
import com.example.bankcards.repository.UserJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountJpaRepository accountJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public AccountService(AccountJpaRepository accountJpaRepository, UserJpaRepository userJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    public AccountDTO createAccount(Long userId, Long balance, String accountNumber) {
        // Валидация входных данных
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть пустым");
        }
        if (balance == null || balance < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным или пустым");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Номер счета не может быть пустым");
        }
        // Проверка уникальности
        if (accountJpaRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("У этого пользователя аккаунт уже создан");
        }
        if (accountJpaRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("Номер счета уже занят");
        }
        UserJpa userJpa = userJpaRepository.findById(userId).orElseThrow(()->new RuntimeException("Not find user"));

        // Создание аккаунта
        AccountJpa accountJpa = new AccountJpa(userJpa, balance, accountNumber);
        accountJpaRepository.save(accountJpa);
        return new AccountDTO(
                userId,
                balance,
                accountNumber
        );

    }

    public AccountDTO updateAccount(Long userId, Long balance, String accountNumber) {
        // Валидация входных данных
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть пустым");
        }
        if (balance == null || balance < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным или пустым");
        }
        if (!accountJpaRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("У этого пользователя нет аккаунта");
        }
        UserJpa userJpa = userJpaRepository.findById(userId).orElseThrow(()->new RuntimeException("Not find user"));
        // Создание аккаунта
        accountJpaRepository.updateById(userId, balance);
        return new AccountDTO(
                userId,
                balance,
                accountNumber
        );
    }

    public String deleteAccount(Long userId) {
        if (!accountJpaRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("У этого пользователя нет аккаунта");
        }
        accountJpaRepository.deleteById(userId);
        return "Ok";
    }
}
