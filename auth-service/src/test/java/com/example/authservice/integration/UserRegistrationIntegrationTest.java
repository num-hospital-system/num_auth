package com.example.authservice.integration;

//import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // @Test
    // @DisplayName("Хэрэглэгч амжилттай бүртгэх тест")
    // void testSuccessfulUserRegistration() throws Exception {
    //     RegisterRequest registerRequest = new RegisterRequest();
    //     registerRequest.setSisiId("22B1NUM3392");
    //     registerRequest.setPassword("22B1NUM3392");
    //     List<String> roles = new ArrayList<>();
    //     roles.add("ROLE_USER");
    //     registerRequest.setRoles(roles);

    //     MvcResult result = mockMvc.perform(post("/auth/register")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(registerRequest)))
    //             .andExpect(status().isOk())
    //             .andReturn();

    //     AuthResponse response = objectMapper.readValue(
    //             result.getResponse().getContentAsString(),
    //             AuthResponse.class
    //     );

    //     assertNotNull(response);
    //     assertEquals("22B1NUM3392", response.getSisiId());
    //     assertNotNull(response.getToken());
    //     assertTrue(response.getRoles().contains("ROLE_USER"));

    //     User savedUser = userRepository.findBySisiId("22B1NUM3392").orElse(null);
    //     assertNotNull(savedUser);
    //     assertEquals("22B1NUM3392", savedUser.getSisiId());
    //     assertTrue(savedUser.getRoles().contains("ROLE_USER"));
    // }

    @Test
    @DisplayName("Давхардсан хэрэглэгч бүртгэх үед алдаа гарах тест")
    void testDuplicateUserRegistration() throws Exception {
        User existingUser = User.builder()
                .sisiId("22B1NUM3333")
                .password("22B1NUM3333")
                .roles(List.of("ROLE_USER"))
                .build();
        userRepository.save(existingUser);

        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setSisiId("22B1NUM3333");
        duplicateRequest.setPassword("22B1NUM3333");
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        duplicateRequest.setRoles(roles);

        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getContentAsString();
        assertTrue(errorMessage.contains("Хэрэглэгч аль хэдийн бүртгэгдсэн байна"));
    }

    @Test
    @DisplayName("existsBySisiId шалгалт зөв ажиллаж байгаа эсэхийг шалгах тест")
    void testExistsBySisiId() {
        String sisiId = "22B1NUM4444";
        
        boolean existsBefore = userRepository.existsBySisiId(sisiId);
        
        assertFalse(existsBefore);
        
        User user = User.builder()
                .sisiId(sisiId)
                .password("22B1NUM4444")
                .roles(List.of("ROLE_USER"))
                .build();
        userRepository.save(user);
        
        boolean existsAfter = userRepository.existsBySisiId(sisiId);
        
        assertTrue(existsAfter);
    }
}
