package com.example.LukeriaFrontendApplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {

    private Long id;

    private String path;
    private String fileName;
    private String contentType;
    private InputStream fileInputStream;

}