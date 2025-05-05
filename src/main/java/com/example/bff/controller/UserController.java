package com.example.bff.controller;

import com.example.bff.model.User;
import com.example.bff.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;

    /**
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            LOGGER.info("Recibida solicitud para obtener todos los usuarios");
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener usuarios: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener usuarios");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crear un nuevo usuario
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            LOGGER.info("Recibida solicitud para crear usuario: " + user.getUsername());
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.severe("Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al crear usuario");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}