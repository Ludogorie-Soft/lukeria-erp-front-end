package com.example.LukeriaFrontendApplication.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenBodyDTO {
    private String refreshToken;
}
