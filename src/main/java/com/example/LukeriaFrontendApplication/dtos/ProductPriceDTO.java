package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceDTO {
    private ProductDTO product;
    private BigDecimal price;
}
