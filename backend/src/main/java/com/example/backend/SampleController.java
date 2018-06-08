package com.example.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Value("${vcap.application.name:localMachine}")
    private String applicationName;

    @Value("${vcap.application.space_name:localMachine}")
    private String spaceName;

    @RequestMapping("/call")
    public String backendCall(){
        return "Hello from backend call located at: " + applicationName + "@" + spaceName;
    }
}
