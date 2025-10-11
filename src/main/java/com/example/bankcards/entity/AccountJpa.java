package com.example.bankcards.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "account")
@Entity
@Data
public class AccountJpa {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accountId;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private UserJpa userJpa;
    @Column(name = "balance")
    private Long balance;
    @Column(name = "account_number", length = 100)
    private String accountNumber;
    @Column(name = "cteate_at")
    private LocalDateTime createAt = LocalDateTime.now();
    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionJpa> fromTransations = new ArrayList<>();
   @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionJpa> toTransactions = new ArrayList<>();
    public AccountJpa(UserJpa userJpa, Long balance, String accountNumber) {
        this.userJpa = userJpa;
        this.balance = balance;
        this.accountNumber = accountNumber;
    }
}
