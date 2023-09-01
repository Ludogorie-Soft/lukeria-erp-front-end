package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String name;
    private byte[] data;
    private String mimeType;
}