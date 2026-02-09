package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableJpaAuditing
@SpringBootApplication
public class ReactBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactBackendApplication.class, args);
	}

}
