package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientUserHelperDTO {
    private ClientDTO clientId;
    private UserDTO userId;
    private Long id;
}
