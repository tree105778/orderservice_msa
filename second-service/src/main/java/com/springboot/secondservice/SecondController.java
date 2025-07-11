package com.springboot.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second-service")
@Slf4j
public class SecondController {
    @GetMapping("/welcome")
    public String welcome() {
        log.info("/welcome: GET");
        return "Welcome to Second Service";
    }
}
