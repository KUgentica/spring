package com.example.kugentica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KugenticaApplication {

  public static void main(String[] args) {
    SpringApplication.run(KugenticaApplication.class, args);
  }
}
