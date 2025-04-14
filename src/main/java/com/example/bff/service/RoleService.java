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

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RestTemplate restTemplate;
    
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
                new ParameterizedTypeReference<List<Role>>() {}
        );
        return response.getBody();
    }
    
    /**
     * Crea un nuevo rol
     */
    public Role createRole(Role role) {
        HttpEntity<Role> requestEntity = new HttpEntity<>(role);
        return restTemplate.postForObject(createRoleUrl, requestEntity, Role.class);
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
                new ParameterizedTypeReference<List<User>>() {}
        );
        return response.getBody();
    }
    
    /**
     * Implementar más métodos según sea necesario
     */
}