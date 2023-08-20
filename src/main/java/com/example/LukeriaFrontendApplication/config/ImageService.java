package com.example.LukeriaFrontendApplication.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
@FeignClient(name = "lukeria-erp-images", url = "http://localhost:8088/api/v1/images")
public interface ImageService {

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadImage(MultipartFile file);
    @GetMapping("/{imageName}")
    byte[] getImage(@PathVariable String imageName);
}

