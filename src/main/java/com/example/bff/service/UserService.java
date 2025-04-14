package com.example.bff.service;

import com.example.bff.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${azure.function.url.get-allusers}")
    private String getAllUsersUrl;
    
    @Value("${azure.function.url.create-user}")
    private String createUserUrl;
    
    /**
     * Obtiene todos los usuarios
     */
    public List<User> getAllUsers() {
        ResponseEntity<List<User>> response = restTemplate.exchange(
                getAllUsersUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );
        return response.getBody();
    }
    
    /**
     * Crea un nuevo usuario
     */
    public User createUser(User user) {
        HttpEntity<User> requestEntity = new HttpEntity<>(user);
        return restTemplate.postForObject(createUserUrl, requestEntity, User.class);
    }
    
    /**
     * Implementar más métodos según sea necesario
     */
}