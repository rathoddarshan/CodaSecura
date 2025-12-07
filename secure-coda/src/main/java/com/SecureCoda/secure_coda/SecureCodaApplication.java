package com.SecureCoda.secure_coda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SecureCodaApplication {

	public static void main(String[] args) {

        SpringApplication.run(SecureCodaApplication.class, args);
	}

}
