package com.kve.quartz_example.springboot_2;

import com.kve.quartz_example.springboot_1.JobBean;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springboot 方式二
 * @author: hujing39
 * @date: 2022-02-22
 */
@Configuration
public class JobConfig {
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
