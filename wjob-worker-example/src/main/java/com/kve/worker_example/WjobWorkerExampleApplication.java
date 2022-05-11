package com.kve.worker_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ={"com.kve.common","com.kve.worker", "com.kve.worker_example"})
class WjobWorkerExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WjobWorkerExampleApplication.class, args);
	}

}
