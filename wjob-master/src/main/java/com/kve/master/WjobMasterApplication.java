package com.kve.master;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages ={"com.kve.common","com.kve.master"})
@MapperScan(basePackages = {"com.kve.common"})
class WjobMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(WjobMasterApplication.class, args);
	}

}
