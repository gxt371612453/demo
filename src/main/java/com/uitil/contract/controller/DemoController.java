package com.uitil.contract.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 */
@RestController
public class DemoController {

    @RequestMapping(value ="/")
    public String index() {
        return "hello world";
    }

    /**
     * 启动测试方法
     * @return
     */
    @RequestMapping(value = "/hello", method = {RequestMethod.GET, RequestMethod.POST})
    public String hello() {
        return "hello Spring boot!";
    }



}
