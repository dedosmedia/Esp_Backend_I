package com.dh.feignclient.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="config-client")
public interface MessageClient {

    @GetMapping("/message")
    String getMessage();


}
