package com.bcaf.bcapay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.bcaf.bcapay")
@EntityScan(basePackages = "com.bcaf.bcapay.models")
@EnableJpaRepositories(basePackages = "com.bcaf.bcapay.repositories")
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
