package com.kve.worker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages ={"com.kve.common","com.kve.worker"})
@MapperScan(basePackages = {"com.kve.common"})
public class WjobWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WjobWorkerApplication.class, args);
	}

}
