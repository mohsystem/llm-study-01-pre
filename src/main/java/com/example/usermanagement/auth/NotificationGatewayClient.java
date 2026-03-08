package com.example.usermanagement.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationGatewayClient {
    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public NotificationGatewayClient(RestTemplateBuilder builder,
                                     @Value("${notification.gateway.url:http://localhost:8081/api/notify/sms}") String gatewayUrl) {
        this.restTemplate = builder.build();
        this.gatewayUrl = gatewayUrl;
    }

    public boolean sendOtp(String destination, String otp) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    gatewayUrl,
                    new HttpEntity<>(Map.of("destination", destination, "message", "Your OTP is " + otp)),
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
