package com.example.bankcards.dto;

import com.example.bankcards.util.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String surName;
    private String patronymic;
    private String email;
    private String telephone;

    public UserDTO(String username, String firstName, String surName, String patronymic, String email, String telephone) {
        this.username = username;
        this.firstName = firstName;
        this.surName = surName;
        this.patronymic = patronymic;
        this.email = email;
        this.telephone = telephone;
    }

    public UserDTO() {

    }
}
