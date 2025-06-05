package com.maven.Rapido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class RapidoApplication {
	public static void main(String[] args) {
		SpringApplication.run(RapidoApplication.class, args);
		System.out.println("Rapido Project Running Successfully-------------");
	}

}
