package com.example.bff.service;

import com.example.bff.model.Role;
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
import java.util.stream.Collectors;

@Service
public class RoleService {
    private static final Logger LOGGER = Logger.getLogger(RoleService.class.getName());

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EventGridService eventGridService;

    @Value("${azure.function.url.get-allroles}")
    private String getAllRolesUrl;

    @Value("${azure.function.url.create-role}")
    private String createRoleUrl;

    @Value("${azure.function.url.get-users-by-role}")
    private String getUsersByRoleUrl;

    /**
     * Obtiene todos los roles
     */
    public List<Role> getAllRoles() {
        ResponseEntity<List<Role>> response = restTemplate.exchange(
                getAllRolesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Role>>() {
                });
        return response.getBody();
    }

    /**
     * Crea un nuevo rol
     */
    public Role createRole(Role role) {
        HttpEntity<Role> requestEntity = new HttpEntity<>(role);
        Role createdRole = restTemplate.postForObject(createRoleUrl, requestEntity, Role.class);

        // return restTemplate.postForObject(createRoleUrl, requestEntity, Role.class);
        eventGridService.publishEvent(
                "RoleCreated",
                "roles/create",
                createdRole);

        return createdRole;
    }

    /**
     * Obtiene usuarios por rol
     */
    public List<User> getUsersByRoleId(Long roleId) {
        String url = getUsersByRoleUrl.replace("{roleId}", roleId.toString());
        ResponseEntity<List<User>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                });
        return response.getBody();
    }

    /**
     * Elimina un rol
     * Esto eliminará el rol y también actualizará todos los usuarios que tenían
     * este rol
     */
    public boolean deleteRole(Long roleId) {
        String deleteRoleUrl = this.getAllRolesUrl + "/" + roleId; // Construye la URL para eliminar

        try {
            // Primero obtén los usuarios afectados para incluirlos en los datos del evento
            List<User> affectedUsers = getUsersByRoleId(roleId);

            // Elimina el rol
            restTemplate.delete(deleteRoleUrl);

            // Publicar evento después de eliminar el rol
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("roleId", roleId);
            eventData.put("affectedUserIds", affectedUsers.stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList()));

            eventGridService.publishEvent(
                    "RoleDeleted",
                    "roles/delete",
                    eventData);

            return true;
        } catch (Exception e) {
            LOGGER.severe("Error eliminando rol: " + e.getMessage());
            return false;
        }
    }
    /**
     * Implementar más métodos según sea necesario
     */
}