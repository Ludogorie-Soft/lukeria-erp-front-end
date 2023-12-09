package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Long clientId;
    private Date orderDate;
    private boolean invoiced;
}
