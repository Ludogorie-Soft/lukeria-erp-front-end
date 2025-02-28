package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturedProductHelperDTO {

    private Long id;
    private ProductDTO product;
    private int quantity;
    private LocalDate manufactureDate;


}
