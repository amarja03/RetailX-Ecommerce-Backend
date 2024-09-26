package com.example.eshopee.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceUrl = "http://localhost:8080/actuator/health";

    @Scheduled(fixedRate =  14*60*1000)
    public void sendHeartBeat() {
        try{
            restTemplate.getForObject(serviceUrl, String.class);
        } catch (Exception e) {
            System.out.println("Service is down");
        }
    }
}
