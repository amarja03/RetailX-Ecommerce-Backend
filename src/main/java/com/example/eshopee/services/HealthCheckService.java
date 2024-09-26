package com.example.eshopee.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate =  14*60*1000)
    public void sendHeartBeat() {
        try{
            String serviceUrl = "https://eshopee-9e9z.onrender.com/api/health";
            restTemplate.getForObject(serviceUrl, String.class);
        } catch (Exception e) {
            System.out.println("Service is down");
        }
    }
}
