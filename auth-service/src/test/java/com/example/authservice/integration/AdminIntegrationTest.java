package com.example.authservice.integration;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // test профайл ашиглаж байгаа бол
public class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll(); // Өмнөх хэрэглэгчдийг цэвэрлэх

        // Админ хэрэглэгч үүсгэх
        RegisterRequest admin = new RegisterRequest();
        admin.setSisiId("admin");
        admin.setPassword("adminpass");
        admin.setRoles(List.of("ROLE_ADMIN"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk());

        // Нэвтрэх болон токен авах
        AuthRequest login = new AuthRequest();
        login.setSisiId("admin");
        login.setPassword("adminpass");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        adminToken = response.getToken();
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll(); // Тестийн дараа өгөгдлийг цэвэрлэх
    }

    /**
     * Админ хэрэглэгч бусдаас эрх хасах тест
     */
    @Test
    @DisplayName("Админ хэрэглэгч бусдаас эрх хасах")
    void testAdminRemovesUserRole() throws Exception {
        // Хэрэглэгч үүсгэх
        RegisterRequest user = new RegisterRequest();
        user.setSisiId("roleRemoveUser");
        user.setPassword("password123");
        user.setRoles(List.of("ROLE_USER", "ROLE_MANAGER"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Админ эрх хасах
        mockMvc.perform(put("/auth/remove-role")
                .header("Authorization", "Bearer " + adminToken)
                .param("sisiId", "roleRemoveUser")
                .param("role", "ROLE_MANAGER"))
                .andExpect(status().isOk());

        // Хэрэглэгчийн мэдээлэл шалгах
        User updated = userRepository.findBySisiId("roleRemoveUser").orElseThrow();
        assertFalse(updated.getRoles().contains("ROLE_MANAGER"));
    }

    /**
     * Админ хэрэглэгч хэрэглэгчийг устгах
     */
    @Test
    @DisplayName("Админ хэрэглэгч бусдыг устгах")
    void testAdminDeletesUser() throws Exception {
        // Хэрэглэгч үүсгэх
        RegisterRequest user = new RegisterRequest();
        user.setSisiId("deleteUser");
        user.setPassword("password123");
        user.setRoles(List.of("ROLE_USER"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // ID авах
        String userId = userRepository.findBySisiId("deleteUser").orElseThrow().getId();

        // Админ хэрэглэгч устгах
        mockMvc.perform(delete("/auth/users/" + userId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Устгагдсан эсэхийг шалгах
        assertFalse(userRepository.findBySisiId("deleteUser").isPresent());
    }

    /**
     * Админ биш хэрэглэгч эрх хасах оролдлого хийхэд алдаа гарна
     */
    @Test
    @DisplayName("Админ биш хэрэглэгч эрх хасах оролдлого хийхэд алдаа гарна")
    void testNonAdminCannotRemoveRole() throws Exception {
        // Хэрэглэгч үүсгэх
        RegisterRequest user = new RegisterRequest();
        user.setSisiId("nonAdmin");
        user.setPassword("userpass");
        user.setRoles(List.of("ROLE_USER"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Нэвтрэх
        AuthRequest login = new AuthRequest();
        login.setSisiId("nonAdmin");
        login.setPassword("userpass");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String userToken = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Админ биш хэрэглэгч эрх хасах оролдлого хийхэд 403 Forbidden буцна
        mockMvc.perform(put("/auth/remove-role")
                .header("Authorization", "Bearer " + userToken)
                .param("sisiId", "admin")
                .param("role", "ROLE_ADMIN"))
                .andExpect(status().isForbidden());
    }
}
