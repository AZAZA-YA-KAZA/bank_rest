package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.UserJpa;
import com.example.bankcards.util.request.UserRequest;
import com.example.bankcards.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.createUser(
                userRequest.username(), userRequest.firstName(), userRequest.surName(),
                userRequest.patronymic(), userRequest.email(), userRequest.telephone(),
                userRequest.password(), userRequest.role()));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId) or hasRole('ADMIN')")
    public ResponseEntity<UserJpa> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping("/update/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId) or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(
                userId, userRequest.username(), userRequest.firstName(), userRequest.surName(),
                userRequest.patronymic(), userRequest.email(), userRequest.telephone(),
                userRequest.password(), userRequest.role()));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("@dataSecurityService.isOwner(#userId) or hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
