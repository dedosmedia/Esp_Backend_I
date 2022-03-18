package com.dh.feignclient.clients;

import com.dh.feignclient.configurations.CustomLoadBalancerConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="config-client")
@LoadBalancerClient(name ="config-client", configuration = CustomLoadBalancerConfiguration.class)
public interface MessageClient {

    @GetMapping("/message")
    ResponseEntity<String> getMessage();


}
