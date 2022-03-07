package com.kve.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: hujing39
 * @date: 2022-03-05
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @RequestMapping("/index")
    public String index(){
        return "index";  //视图重定向hello.jsp
    }
}