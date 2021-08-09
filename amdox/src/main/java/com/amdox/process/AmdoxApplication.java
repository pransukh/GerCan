package com.amdox.process;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {Config.class})
public class AmdoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmdoxApplication.class, args);
	}

}
