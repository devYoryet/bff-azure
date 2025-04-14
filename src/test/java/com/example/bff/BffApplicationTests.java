package com.example.bff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "azure.function.url.get-allusers=https://get-allusers.azurewebsites.net/api/users",
        "azure.function.url.create-user=https://create-user-fx.azurewebsites.net/api/users",
        "azure.function.url.get-allroles=https://get-allroles.azurewebsites.net/api/roles",
        "azure.function.url.create-role=https://create-role.azurewebsites.net/api/roles",
        "azure.function.url.get-users-by-role=https://get-users-by-rol.azurewebsites.net/api/roles/{roleId}/users"
})
class BffApplicationTests {

    @Test
    void contextLoads() {
        // Test vacío que verifica que el contexto de la aplicación se carga
        // correctamente
    }
}