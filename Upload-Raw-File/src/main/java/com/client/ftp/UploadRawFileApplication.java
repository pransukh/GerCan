package com.client.ftp;

import com.client.config.ClientConfig;
import com.client.config.FTPConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(value = {FTPConfig.class,ClientConfig.class})
public class UploadRawFileApplication {
public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(UploadRawFileApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate()
	{
		return new RestTemplate();
	}
}

