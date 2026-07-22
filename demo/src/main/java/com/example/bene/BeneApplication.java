package com.example.bene;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeneApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeneApplication.class, args);
	}

}
