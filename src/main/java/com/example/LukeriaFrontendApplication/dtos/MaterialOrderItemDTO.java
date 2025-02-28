package com.example.LukeriaFrontendApplication.dtos;

import com.example.LukeriaFrontendApplication.enums.MaterialType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderItemDTO {

    private Long id;

    @NotNull(message = "Material type is required")
    private MaterialType materialType;

    @NotNull(message = "Material ID is required")
    @Min(value = 1, message = "Material ID must be a positive number")
    private Long materialId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private String materialName;
    private String photo;
}