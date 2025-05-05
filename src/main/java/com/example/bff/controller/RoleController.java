package com.example.bff.controller;

import com.example.bff.model.Role;
import com.example.bff.model.User;
import com.example.bff.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private static final Logger LOGGER = Logger.getLogger(RoleController.class.getName());

    @Autowired
    private RoleService roleService;

    /**
     * Eliminar un rol
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        try {
            LOGGER.info("Recibida solicitud para eliminar rol: " + roleId);
            boolean deleted = roleService.deleteRole(roleId);

            if (deleted) {
                return new ResponseEntity<>(Map.of("success", true, "message", "Rol eliminado correctamente"),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("success", false, "message", "No se pudo eliminar el rol"),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOGGER.severe("Error al eliminar rol: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al eliminar rol");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener todos los roles
     */
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            LOGGER.info("Recibida solicitud para obtener todos los roles");
            List<Role> roles = roleService.getAllRoles();
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener roles: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener roles");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crear un nuevo rol
     */
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            LOGGER.info("Recibida solicitud para crear rol: " + role.getRoleName());
            Role createdRole = roleService.createRole(role);
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.severe("Error al crear rol: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al crear rol");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener usuarios por rol
     */
    @GetMapping("/{roleId}/users")
    public ResponseEntity<?> getUsersByRoleId(@PathVariable Long roleId) {
        try {
            LOGGER.info("Recibida solicitud para obtener usuarios del rol ID: " + roleId);
            List<User> users = roleService.getUsersByRoleId(roleId);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener usuarios por rol: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener usuarios por rol");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}