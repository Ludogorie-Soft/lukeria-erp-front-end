package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.models.User;
import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-user", url = "http://localhost:8088/api/v1/user")
public interface UserClient {
    @GetMapping
    List<UserDTO> getAllUsers();

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable(name = "id") Long id);

    @PostMapping
    UserDTO createUser(@Valid @RequestBody User user);

    @PutMapping("/{id}")
    UserDTO updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO);

    @DeleteMapping("/{id}")
    String deleteUserById(@PathVariable("id") Long id);

    @PutMapping("restore/{id}")
    UserDTO restoreUser(@PathVariable("id") Long id);
}
