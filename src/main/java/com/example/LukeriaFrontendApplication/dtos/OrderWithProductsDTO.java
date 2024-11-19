package com.example.LukeriaFrontendApplication.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderWithProductsDTO {
    private OrderDTO orderDTO;
    private List<OrderProductDTO> orderProductDTOs;

    // Constructor, getters, and setters
    public OrderWithProductsDTO(OrderDTO orderDTO, List<OrderProductDTO> orderProductDTOs) {
        this.orderDTO = orderDTO;
        this.orderProductDTOs = orderProductDTOs;
    }

    public void setOrderDTO(OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
    }

    public void setOrderProductDTOs(List<OrderProductDTO> orderProductDTOs) {
        this.orderProductDTOs = orderProductDTOs;
    }
}
