package com.example.bankcards.entity;

import com.example.bankcards.util.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "transaction")
@Entity
@Data
public class TransactionJpa {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id", referencedColumnName = "account_id")
    private AccountJpa fromAccountId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_account_id", referencedColumnName = "account_id")
    private AccountJpa toAccountId;
    @Column(name = "amount")
    private Long amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;
    @Column(name = "cteate_at")
    private LocalDateTime createAt = LocalDateTime.now();

    public TransactionJpa(AccountJpa fromAccountId, AccountJpa toAccountId, Long amount, TransactionType type) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
    }
}
