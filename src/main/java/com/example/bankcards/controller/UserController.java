package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.util.request.UserRequest;
import com.example.bankcards.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> createUser(
            @RequestBody UserRequest userRequest
            ) {
        return ResponseEntity.ok(userService.createUser(userRequest.username(),
                userRequest.firstName(), userRequest.surName(),
                userRequest.patronymic(), userRequest.email(),
                userRequest.telephone(), userRequest.password(),
                userRequest.role()));
    }
}
