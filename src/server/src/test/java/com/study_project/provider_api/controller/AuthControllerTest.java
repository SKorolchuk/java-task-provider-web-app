package com.study_project.provider_api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционное тестирование REST-эндпоинтов аутентификации.
 * Проверяет корректность обработки входящих данных, работу сессий,
 * генерацию ролей и интеграцию с подсистемой серверной локализации (i18n).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private MessageSource messageSource;

    @Test
    @DisplayName("Успешный вход в систему с возвратом имени и роли пользователя")
    void login_Success() throws Exception {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("admin_chief");

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(mockAuth.getAuthorities()).thenAnswer(invocation -> authorities);

        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        String jsonRequestBody = "{\"username\": \"admin_chief\", \"password\": \"password\"}";

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin_chief"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("Ошибка 401 Unauthorized при неверном логине или пароле с учетом локализации")
    void login_Failure_WithInvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        String expectedErrorMessage = "Неверное имя пользователя или пароль";
        when(messageSource.getMessage(eq("auth.bad.credentials"), any(), any(Locale.class)))
                .thenReturn(expectedErrorMessage);

        String jsonRequestBody = "{\"username\": \"wrong_user\", \"password\": \"wrong_pass\"}";

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(messageSource, times(1)).getMessage(eq("auth.bad.credentials"), any(), any(Locale.class));
    }
}
