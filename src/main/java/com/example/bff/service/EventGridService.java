package com.example.bff.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Service
public class EventGridService {
    private static final Logger LOGGER = Logger.getLogger(EventGridService.class.getName());

    private final RestTemplate restTemplate;

    @Value("${azure.event.grid.endpoint}")
    private String eventGridEndpoint;

    @Value("${azure.event.grid.key}")
    private String eventGridKey;

    public EventGridService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void publishEvent(String eventType, String subject, Object data) {
        try {
            List<Map<String, Object>> events = new ArrayList<>();
            Map<String, Object> event = new HashMap<>();

            event.put("id", UUID.randomUUID().toString());
            event.put("subject", subject);
            event.put("eventType", eventType);
            event.put("eventTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
            event.put("data", data);
            event.put("dataVersion", "1.0");

            events.add(event);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("aeg-sas-key", eventGridKey);

            HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(events, headers);

            restTemplate.postForEntity(eventGridEndpoint, requestEntity, String.class);
            LOGGER.info("Event published successfully: " + eventType);
        } catch (Exception e) {
            LOGGER.severe("Error publishing event: " + e.getMessage());
        }
    }
}