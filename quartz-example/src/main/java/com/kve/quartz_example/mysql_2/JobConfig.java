package com.kve.quartz_example.mysql_2;

import com.kve.quartz_example.springboot_1.JobBean;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;

/**
 * mysql_2
 * @author: hujing39
 * @date: 2022-02-22
 */
@Configuration
public class JobConfig {
    @Bean
    @QuartzDataSource
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setUsername();
//        dataSource.setPassword();
//        dataSource.setUrl();
        return dataSource;
    }

    @Bean
    public JobDetail job() {
        return JobBuilder.newJob(JobBean.class)
                .storeDurably()
                .withIdentity("job")
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob("job")
                .startNow()
                .build();
    }
}
