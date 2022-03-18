package com.dh.configclient.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class TestController {

    @Value("${message:Mensaje por defecto}")
    private String message;

    @Value("${server.port}")
    private Integer port;

    @GetMapping("/message")
    private String getMessage(HttpServletResponse response){

        response.addHeader("puerto", String.valueOf(port));
        log.info(String.valueOf(this.port));
        return String.format("El puerto es %s y el mensaje %s", this.port, this.message);
    }

}
