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
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EventGridService {
    private static final Logger LOGGER = Logger.getLogger(EventGridService.class.getName());

    private final RestTemplate restTemplate;

    // Proporciona valores predeterminados en caso de que no estén en
    // application.properties
    @Value("${azure.event.grid.endpoint:https://evento1.eastus2-1.eventgrid.azure.net/api/events}")
    private String eventGridEndpoint;

    @Value("${azure.event.grid.key:1akKzfiKxGIffXkVvohg6Ke0hcMsou59OWSJpwYPd6v3DYpe3CueJQQJ99BEACHYHv6XJ3w3AAABAZEG1vsr}")
    private String eventGridKey;

    public EventGridService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Log para verificar que el servicio se inicializó correctamente
        LOGGER.info("EventGridService inicializado con endpoint: " + eventGridEndpoint);
    }

    public boolean publishEvent(String eventType, String subject, Object data) {
        try {
            LOGGER.info("Intentando publicar evento: " + eventType);

            // Validar que tengamos los datos necesarios
            if (eventGridEndpoint == null || eventGridEndpoint.isEmpty()) {
                LOGGER.severe("Error: eventGridEndpoint no está configurado");
                return false;
            }

            if (eventGridKey == null || eventGridKey.isEmpty()) {
                LOGGER.severe("Error: eventGridKey no está configurado");
                return false;
            }

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

            LOGGER.info("Enviando evento a: " + eventGridEndpoint);
            restTemplate.postForEntity(eventGridEndpoint, requestEntity, String.class);
            LOGGER.info("Evento publicado exitosamente: " + eventType);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error publicando evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}