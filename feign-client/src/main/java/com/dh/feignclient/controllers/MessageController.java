package com.dh.feignclient.controllers;


import com.dh.feignclient.services.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {


    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/mymessage")
    public String getMessage(){

        return this.messageService.getMessage();
    }

}
