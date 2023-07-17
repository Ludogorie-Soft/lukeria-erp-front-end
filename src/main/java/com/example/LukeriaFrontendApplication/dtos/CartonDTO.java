package com.example.LukeriaFrontendApplication.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartonDTO {
    private Long id;
    @Size(min = 10, message = "Моля въведете името на кашона с поне 10 символа!")
    private String name;
    @Size(min = 10, message = "Моля въведете размерите на кашона с поне 10 символа!")
    private String size;
    @Min(value = 1, message = "Наличните бройки трябва да бъдат по-големи от 0!")
    private Integer availableQuantity;
    private double price;
}