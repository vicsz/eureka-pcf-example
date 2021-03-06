package com.example.frontend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class SampleController {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate rest;

    @RequestMapping()
    public String helloWorld(){
        return "Hello world!!";
    }

    @RequestMapping("call")
    public String callBackend(){
        return rest.getForObject("http://backend-service/call", String.class);
    }

    @RequestMapping("services")
    public String listServices(){

        StringBuilder output = new StringBuilder();

        List<String> services = discoveryClient.getServices();

        output.append("Number of Services found: " + services.size() + "<br>");

        for (String service: services) {

            List<ServiceInstance> serviceInstances = discoveryClient.getInstances("backend-service");

            output.append("Service found " + service + " with " + serviceInstances.size() + " instances <br>");

            for (ServiceInstance serviceInstance: serviceInstances) {
                output.append("Service host " + serviceInstance.getHost() + "<br>");

            }

        }

        return output.toString();
    }
}
