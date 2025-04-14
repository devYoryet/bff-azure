package com.example.bff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/graphql")
public class GraphQLController {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${azure.function.url.graphql-query}")
    private String graphqlQueryUrl;
    
    @Value("${azure.function.url.graphql-mutation}")
    private String graphqlMutationUrl;
    
    @PostMapping("/query")
    public ResponseEntity<Object> query(@RequestBody Map<String, Object> requestBody) {
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
            Object response = restTemplate.postForObject(graphqlQueryUrl, requestEntity, Object.class);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/mutation")
    public ResponseEntity<Object> mutation(@RequestBody Map<String, Object> requestBody) {
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
            Object response = restTemplate.postForObject(graphqlMutationUrl, requestEntity, Object.class);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}