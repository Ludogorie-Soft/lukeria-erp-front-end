package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturedProductDTO {

    private Long id;
    private Long productId;
    private int quantity;
    private LocalDateTime manufactureDate;

}
