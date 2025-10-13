package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.repository.AccountJpaRepository;
import com.example.bankcards.service.AccountService;
import com.example.bankcards.util.request.AccountRequest;
import com.example.bankcards.util.request.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/api/user/create/account")
    @PreAuthorize("@dataSecurityService.isOwner(#userId)")
    public ResponseEntity<AccountDTO> createAccount(
            @PathVariable Long userId,
            @RequestBody AccountRequest accountRequest
    ) {
        return ResponseEntity.ok(accountService.createAccount(userId, accountRequest.balance(), accountRequest.accountNumber()));
    }

    @PostMapping("/api/user/update/account/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId)")
    public ResponseEntity<AccountDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody AccountRequest accountRequest
    ) {
        return ResponseEntity.ok(accountService.updateAccount(userId, accountRequest.balance(), accountRequest.accountNumber()));
    }

    @DeleteMapping("/api/user/delete/account/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId)")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(accountService.deleteAccount(userId));
    }


}
