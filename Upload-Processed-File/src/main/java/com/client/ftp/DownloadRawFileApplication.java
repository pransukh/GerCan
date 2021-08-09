package com.client.ftp;

import com.client.config.MdoxConfig;
import com.client.config.FTPConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@EnableConfigurationProperties(value = {FTPConfig.class, MdoxConfig.class})
public class DownloadRawFileApplication {
public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DownloadRawFileApplication.class, args);
	}


}

