package com.bcaf.finapay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@SpringBootApplication
@ComponentScan(basePackages = "com.bcaf.finapay")
@EntityScan(basePackages = "com.bcaf.finapay.models")
@EnableJpaRepositories(basePackages = "com.bcaf.finapay.repositories")
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); 
        return factory.createMultipartConfig();
    }

}
