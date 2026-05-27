package com.school.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.auth.dto.LoginRequest;
import com.school.auth.dto.RegisterRequest;
import com.school.common.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void register_thenLogin_shouldReturnToken() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail("test.teacher@school.com");
        reg.setPassword("Password123!");
        reg.setFirstName("Test");
        reg.setLastName("Teacher");
        reg.setRole(Role.TEACHER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists());

        LoginRequest login = new LoginRequest();
        login.setEmail("test.teacher@school.com");
        login.setPassword("Password123!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.email").value("test.teacher@school.com"));
    }

    @Test
    void login_withBadCredentials_shouldReturn401() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("nobody@school.com");
        login.setPassword("WrongPass");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_withDuplicateEmail_shouldReturn400() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail("dup@school.com");
        reg.setPassword("Password123!");
        reg.setFirstName("A");
        reg.setLastName("B");
        reg.setRole(Role.STUDENT);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
