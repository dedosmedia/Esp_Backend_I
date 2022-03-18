package com.dh.feignclient.services;


import com.dh.feignclient.clients.MessageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private MessageClient client;

    public MessageService(MessageClient client) {
        this.client = client;
    }


    public ResponseEntity<String> getMessage(){

        return client.getMessage();
    }

}
