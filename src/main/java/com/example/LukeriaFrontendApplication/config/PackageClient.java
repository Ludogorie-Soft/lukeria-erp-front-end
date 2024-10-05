package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-package", url = "${backend.base-url}/package")

public interface PackageClient {
    @GetMapping
    List<PackageDTO> getAllPackages(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    PackageDTO getPackageById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);
    @GetMapping("/materials/{id}")
     void getAllMaterialsForPackageById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) ;

    @PostMapping
    PackageDTO createPackage(@Valid @RequestBody PackageDTO packageDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updatePackage(@PathVariable("id") Long id, @RequestBody PackageDTO packageDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deletePackageById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);
}
