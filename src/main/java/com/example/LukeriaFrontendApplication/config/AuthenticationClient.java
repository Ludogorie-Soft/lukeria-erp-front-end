package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.*;
import org.apache.coyote.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;

@FeignClient(name = "lukeria-erp-auth", url = "http://localhost:8088/api/v1/auth")
public interface AuthenticationClient {
    @PostMapping("/register")
    AuthenticationResponse register(@RequestBody RegisterRequest request);

    @PostMapping("/authenticate")
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request);

    @PostMapping("/refresh-token")
    AuthenticationResponse refreshToken(@RequestBody RefreshTokenBodyDTO refreshTokenBody) throws IOException;
    @GetMapping("/logout")
    void logout (@RequestHeader("Authorization") String auth);
}
