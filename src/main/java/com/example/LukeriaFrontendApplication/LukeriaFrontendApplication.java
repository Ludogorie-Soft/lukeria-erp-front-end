package com.example.LukeriaFrontendApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LukeriaFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LukeriaFrontendApplication.class, args);
	}

}