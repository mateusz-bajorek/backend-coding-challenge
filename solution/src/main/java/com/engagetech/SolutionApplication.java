package com.engagetech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class SolutionApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SolutionApplication.class, args);
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
    }
}
