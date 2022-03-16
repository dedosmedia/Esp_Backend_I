package com.dh.configclient.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${message:Mensaje por defecto}")
    private String message;

    @GetMapping("/message")
    private String getMessage(){
        return this.message;
    }

}
