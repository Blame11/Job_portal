package com.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class JobPortalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalBackendApplication.class, args);
    }

}
