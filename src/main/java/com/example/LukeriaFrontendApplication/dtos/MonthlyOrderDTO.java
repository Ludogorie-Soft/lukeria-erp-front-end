package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyOrderDTO {
    private Long id;
    private Long clientId;
    private Date startDate;
    private Date endDate;
    private boolean invoiced;
}
