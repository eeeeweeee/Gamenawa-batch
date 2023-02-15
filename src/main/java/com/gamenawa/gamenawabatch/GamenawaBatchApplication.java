package com.gamenawa.gamenawabatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableBatchProcessing
@SpringBootApplication
public class GamenawaBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamenawaBatchApplication.class, args);
    }

}
