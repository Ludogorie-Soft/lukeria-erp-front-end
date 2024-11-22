package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemHelper {

    private Long cartItemId;
    private ProductDTO product;
    private int quantity;
    private BigDecimal price;
}
