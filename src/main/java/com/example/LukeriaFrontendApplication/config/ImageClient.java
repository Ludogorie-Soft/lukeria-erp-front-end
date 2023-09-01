package com.example.LukeriaFrontendApplication.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
@FeignClient(name = "lukeria-erp-images", url = "http://localhost:8088/api/v1/images")
public interface ImageClient {

    @PostMapping(value = "uploadImageForPackage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadImageForPackage(MultipartFile file);
    @GetMapping("/{imageName}")
    byte[] getImage(@PathVariable String imageName);
    @PostMapping(value = "/editImageForPackage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void editImageForPackage(MultipartFile file, @RequestParam("packageId") Long packageId);
    @PostMapping(value = "uploadImageForPlate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadImageForPlate(MultipartFile file);
    @PostMapping(value = "/editImageForPlate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void editImageForPlate(MultipartFile file, @RequestParam("plateId") Long plateId);
}

