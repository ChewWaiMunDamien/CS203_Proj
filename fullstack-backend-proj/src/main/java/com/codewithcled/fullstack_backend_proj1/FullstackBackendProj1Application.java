package com.codewithcled.fullstack_backend_proj1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FullstackBackendProj1Application {

	public static void main(String[] args) {
		SpringApplication.run(FullstackBackendProj1Application.class, args);
	}

}
