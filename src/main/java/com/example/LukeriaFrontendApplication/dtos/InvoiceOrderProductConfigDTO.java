package com.example.LukeriaFrontendApplication.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class InvoiceOrderProductConfigDTO {
    private List<Long> orderProductIds;
    private Long invoiceId;
    private List<Integer> quantityInputIntList;
    private List<BigDecimal> priceInputBigDecimalList;
}
