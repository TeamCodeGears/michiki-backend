package com.michiki.michiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MichikiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MichikiApplication.class, args);
	}
}

