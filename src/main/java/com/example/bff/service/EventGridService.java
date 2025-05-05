package com.example.bff.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

            String eventId = UUID.randomUUID().toString();
            event.put("id", eventId);
            event.put("subject", subject);
            event.put("eventType", eventType);
            event.put("eventTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
            event.put("data", data);
            event.put("dataVersion", "1.0");

            events.add(event);

            LOGGER.info("Preparando envío de evento: " + eventType + ", ID: " + eventId + ", Asunto: " + subject);
            LOGGER.info("Datos del evento: " + new Gson().toJson(data));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("aeg-sas-key", eventGridKey);

            HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(events, headers);

            LOGGER.info("Enviando evento a: " + eventGridEndpoint);
            ResponseEntity<String> response = restTemplate.postForEntity(eventGridEndpoint, requestEntity,
                    String.class);

            LOGGER.info("Respuesta de Event Grid - Código: " + response.getStatusCodeValue());
            LOGGER.info("Respuesta de Event Grid - Headers: " + response.getHeaders());
            LOGGER.info("Respuesta de Event Grid - Body: " + response.getBody());

            LOGGER.info("Event published successfully: " + eventType);
        } catch (Exception e) {
            LOGGER.severe("Error publishing event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}