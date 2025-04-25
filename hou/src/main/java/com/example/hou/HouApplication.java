package com.example.hou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class HouApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouApplication.class, args);
    }

}
