package com.project.mentoridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MentoridgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentoridgeApplication.class, args);
	}

}
