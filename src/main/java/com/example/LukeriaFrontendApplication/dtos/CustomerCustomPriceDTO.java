package com.example.LukeriaFrontendApplication.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCustomPriceDTO {

    private Long id;

    private Long clientId;

    private Long productId;

    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private BigDecimal price;
}
