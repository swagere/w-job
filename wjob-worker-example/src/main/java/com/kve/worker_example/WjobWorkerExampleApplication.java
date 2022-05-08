package com.kve.worker_example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ={"com.kve.common","com.kve.worker", "com.kve.worker_example"})
@MapperScan(basePackages = {"com.kve.master.mapper"})
class WjobWorkerExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WjobWorkerExampleApplication.class, args);
	}

}
