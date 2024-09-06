package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-user", url = "${backend.base-url}/user")
public interface UserClient {
    @GetMapping
    List<UserDTO> getAllUsers(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    UserDTO createUser(@Valid @RequestBody UserDTO user, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    UserDTO updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteUserById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/me")
    UserDTO findAuthenticatedUser(@RequestHeader("Authorization") String auth);

    @GetMapping("/ifPassMatch")
    Boolean ifPassMatch(UserDTO userDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/change-pass")
    public void changePassword(UserDTO userDTO, @RequestHeader("Authorization") String auth);
}
