package com.example.bff.controller;

import com.example.bff.service.EventGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/graphql")
public class GraphQLController {
    private static final Logger LOGGER = Logger.getLogger(GraphQLController.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EventGridService eventGridService;

    @Value("${azure.function.url.graphql-query}")
    private String graphqlQueryUrl;

    @Value("${azure.function.url.graphql-mutation}")
    private String graphqlMutationUrl;

    @PostMapping("/query")
    public ResponseEntity<Object> query(@RequestBody Map<String, Object> requestBody) {
        try {
            LOGGER.info("Procesando GraphQL query");
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
            Object response = restTemplate.postForObject(graphqlQueryUrl, requestEntity, Object.class);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.severe("Error procesando GraphQL query: " + e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/mutation")
    public ResponseEntity<Object> mutation(@RequestBody Map<String, Object> requestBody) {
        try {
            LOGGER.info("Procesando GraphQL mutation");
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
            Object response = restTemplate.postForObject(graphqlMutationUrl, requestEntity, Object.class);

            // Detectar qué tipo de mutación es para publicar el evento correspondiente
            if (requestBody.containsKey("query")) {
                String query = (String) requestBody.get("query");

                // Detectar creación de usuario
                if (query.contains("createUser") || query.contains("addUser")) {
                    eventGridService.publishEvent(
                            "UserCreated",
                            "graphql/users/create",
                            response);
                }

                // Detectar creación de rol
                if (query.contains("createRole") || query.contains("addRole")) {
                    eventGridService.publishEvent(
                            "RoleCreated",
                            "graphql/roles/create",
                            response);
                }

                // Detectar eliminación de usuario
                else if (query.contains("deleteUser")) {
                    eventGridService.publishEvent(
                            "UserDeleted",
                            "graphql/users/delete",
                            response);
                }
                // Detectar eliminación de rol
                else if (query.contains("deleteRole")) {
                    eventGridService.publishEvent(
                            "RoleDeleted",
                            "graphql/roles/delete",
                            response);
                }
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.severe("Error procesando GraphQL mutation: " + e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}