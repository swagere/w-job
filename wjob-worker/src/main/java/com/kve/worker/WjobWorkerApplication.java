package com.kve.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ={"com.kve.common","com.kve.worker"})
public class WjobWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WjobWorkerApplication.class, args);
	}

}
