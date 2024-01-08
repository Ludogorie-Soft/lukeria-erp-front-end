package com.example.LukeriaFrontendApplication.dtos;

import com.example.LukeriaFrontendApplication.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserDTO {
    private String firstname;

    @JsonProperty("username")
    private String usernameField;
    private String email;
    private Role role;
}
