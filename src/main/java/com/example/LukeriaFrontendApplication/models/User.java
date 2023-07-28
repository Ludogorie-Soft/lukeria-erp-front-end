package com.example.LukeriaFrontendApplication.models;

import com.example.LukeriaFrontendApplication.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    @Size(min = 4)
    private String username;
    @Size(min = 5)
    private String fullName;
    @Email
    private String email;
    private String password;

    private Role role;

    private boolean deleted;
}

