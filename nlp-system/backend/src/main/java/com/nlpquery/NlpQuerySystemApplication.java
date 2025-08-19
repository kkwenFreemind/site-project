package com.nlpquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * NLP Query System Main Application
 * 
 * A system that converts natural language queries to SQL using Google Gemini AI
 * and executes them safely against PostgreSQL database.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class NlpQuerySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NlpQuerySystemApplication.class, args);
    }
}
