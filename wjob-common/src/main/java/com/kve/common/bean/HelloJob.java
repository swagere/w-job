package com.kve.common.bean;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: hujing39
 * @date: 2022-03-16
 */


@Component("HelloJob")
public class HelloJob {
    private static Logger log = LoggerFactory.getLogger(HelloJob.class);

    public void print() {
        log.info("job execute ----------------------->");
        System.out.println("job execute ----------------------->");
    }
}
