package com.alertsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AlertSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertSystemBackendApplication.class, args);
    }

}
