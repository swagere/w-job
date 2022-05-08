package com.kve.worker_example.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("HelloJob")
public class HelloJob {
    private static Logger log = LoggerFactory.getLogger(HelloJob.class);

    public void print() {
        log.info("job execute ----------------------->");
    }
}
