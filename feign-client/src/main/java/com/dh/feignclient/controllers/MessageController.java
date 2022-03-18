package com.dh.feignclient.controllers;


import com.dh.feignclient.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class MessageController {


    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/mymessage")
    public String getMessage(HttpServletResponse r){

        ResponseEntity<String> response = this.messageService.getMessage();

        log.info(String.format("El puerto que recibimos por header es %s", response.getHeaders().get("puerto")));
        r.addHeader("puerto",response.getHeaders().get("puerto").get(0));
        return response.getBody();
    }

}
