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
import org.springframework.beans.factory.annotation.Autowired;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EventGridService eventGridService;

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
                new ParameterizedTypeReference<List<User>>() {
                });
        return response.getBody();
    }

    /**
     * Crea un nuevo usuario
     */
    public User createUser(User user) {
        HttpEntity<User> requestEntity = new HttpEntity<>(user);
        User createdUser = restTemplate.postForObject(createUserUrl, requestEntity, User.class);

        // return restTemplate.postForObject(createUserUrl, requestEntity, User.class);

        // Publicar evento
        eventGridService.publishEvent(
                "UserCreated",
                "users/create",
                createdUser);

        return createdUser;
    }

    public boolean deleteUser(Long userId) {
        String deleteUserUrl = this.getAllUsersUrl + "/" + userId; // Construye la URL para eliminar

        try {
            restTemplate.delete(deleteUserUrl);

            // Publicar evento después de eliminar el usuario
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("userId", userId);
            eventGridService.publishEvent(
                    "UserDeleted",
                    "users/delete",
                    eventData);

            return true;
        } catch (Exception e) {
            LOGGER.severe("Error eliminando usuario: " + e.getMessage());
            return false;
        }
    }
}
