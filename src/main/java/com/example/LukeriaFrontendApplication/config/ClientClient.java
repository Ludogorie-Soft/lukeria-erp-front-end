package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-client", url = "http://localhost:8088/api/v1/client")
public interface ClientClient {
    @GetMapping()
    List<ClientDTO> getAllClients(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    ClientDTO getClientById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    ClientDTO createClient(@Valid @RequestBody ClientDTO clientDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateClient(@PathVariable("id") Long id, @Valid @RequestBody ClientDTO clientDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteClientById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

}
