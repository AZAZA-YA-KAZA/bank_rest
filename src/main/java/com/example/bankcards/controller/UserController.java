package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.util.request.UserRequest;
import com.example.bankcards.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/api/profileSt/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId)")
    public ResponseEntity<UserJpa> userAccess(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PostMapping("/api/user/update/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UserRequest userRequest
            ) {
        return ResponseEntity.ok(userService.updateUser(userId, userRequest.username(),
                userRequest.firstName(), userRequest.surName(),
                userRequest.patronymic(), userRequest.email(),
                userRequest.telephone(), userRequest.password(),
                userRequest.role()));
    }

    @DeleteMapping("/api/user/delete/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId)")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId
            ) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
