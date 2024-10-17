package com.example.LukeriaFrontendApplication.dtos;

import com.example.LukeriaFrontendApplication.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long orderId;
    @NotNull
    private Long clientId;
    @NotNull
    private Long createdByUser;
    @NotEmpty
    private LocalDate orderDate;
    @NotEmpty
    private OrderStatus status;
    @NotEmpty
    private List<CartItemDTO> items;
}
