package com.uitil.contract.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试
 */
@Controller
public class DemoController {

    @RequestMapping(value ="/")
    public String index() {
        return "page.html";
    }


    /**
     * 启动测试方法
     * @return
     */
    @RequestMapping(value = "/hello", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String hello() {
        return "hello Spring boot!";
    }

    /**
     * page页面
     * @return
     */
    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public String page() {
        return "page.html";
    }

    @RequestMapping(value = "/upload", method = {RequestMethod.GET, RequestMethod.POST})
    public String upload() {
        return "upload.html";
    }

    @RequestMapping(value = "/demoFile", method = {RequestMethod.GET, RequestMethod.POST})
    public String demo() {
        return "demo.html";
    }

}
