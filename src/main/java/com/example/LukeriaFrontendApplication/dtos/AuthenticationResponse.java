package com.example.LukeriaFrontendApplication.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PublicUserDTO user;
}
