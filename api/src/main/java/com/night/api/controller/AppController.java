package com.night.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController {
    
    @ResponseBody
    @RequestMapping("/test")
    public String test() {
        return "123";
    }
}
