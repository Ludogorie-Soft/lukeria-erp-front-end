package com.example.LukeriaFrontendApplication.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderHelperForShowDTO {

    private Long id;
    private String name;
    private Long materialId;
    @Min(1)
    private int orderedQuantity;
    @NotBlank
    private String materialType;
    private Integer receivedQuantity;
    private Date arrivalDate;
    private BigDecimal materialPrice;
}
