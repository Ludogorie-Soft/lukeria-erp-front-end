package com.example.LukeriaFrontendApplication.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "lukeria-erp-uploadFile", url = "${backend.base-url}/upload")
public interface UploadFileClient {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFromFile(MultipartFile file, @RequestHeader("Authorization") String auth);
}
