package com.kve.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ={"com.kve.common","com.kve.server","com.kve.rpcClient"})
public class WjobServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WjobServerApplication.class, args);
	}

}
