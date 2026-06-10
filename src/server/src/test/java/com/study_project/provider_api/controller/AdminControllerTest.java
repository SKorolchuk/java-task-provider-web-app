package com.study_project.provider_api.controller;

import com.study_project.provider_api.dto.ClientsPageDto;
import com.study_project.provider_api.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Collections;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Интеграционное тестирование REST-эндпоинтов администратора.
 * Проверяет разграничение прав доступа (RBAC), структуру JSON-ответов DTO
 * и валидацию входящих параметров на стороне сервера.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AdminService adminService;

        @Test
        @DisplayName("Успешное получение страницы клиентов под ролью ADMIN")
        void getAllClients_Success_WhenUserIsAdmin() throws Exception {
                ClientsPageDto mockPageDto = new ClientsPageDto(
                                Collections.emptyList(), 0, 0, 0);

                when(adminService.getClientsPage(0, 5)).thenReturn(mockPageDto);

                mockMvc.perform(get("/api/admin/clients")
                                .param("page", "0")
                                .param("size", "5")
                                .with(user("admin").roles("ADMIN"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.currentPage").value(0))
                                .andExpect(jsonPath("$.totalItems").value(0))
                                .andExpect(jsonPath("$.clients").isArray());

                verify(adminService, times(1)).getClientsPage(0, 5);
        }

        @Test
        @DisplayName("Отказ в доступе (403) при запросе списка клиентов под ролью CLIENT")
        void getAllClients_Forbidden_WhenUserIsClient() throws Exception {
                mockMvc.perform(get("/api/admin/clients")
                                .with(user("client").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Ошибка валидации (400), если передан отрицательный индекс страницы")
        void getAllClients_BadRequest_WhenPageIsNegative() throws Exception {
                mockMvc.perform(get("/api/admin/clients")
                                .param("page", "-1")
                                .param("size", "5")
                                .with(user("admin").roles("ADMIN"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Некорректные параметры запроса"));
        }

        @Test
        @DisplayName("Успешное изменение статуса блокировки клиента администратором")
        void toggleBlockClient_Success() throws Exception {
                doNothing().when(adminService).changeBlockStatus(1L, true);

                mockMvc.perform(patch("/api/admin/clients/1/block")
                                .param("block", "true")
                                .with(user("admin").roles("ADMIN"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Статус блокировки успешно изменен"));

                verify(adminService, times(1)).changeBlockStatus(1L, true);
        }

        @Test
        @DisplayName("Отказ в доступе (403) при попытке блокировки под ролью CLIENT")
        void toggleBlockClient_Forbidden_WhenUserIsClient() throws Exception {
                mockMvc.perform(patch("/api/admin/clients/1/block")
                                .param("block", "true")
                                .with(user("client").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Успешное начисление расхода трафика администратором")
        void chargeClient_Success() throws Exception {
                java.math.BigDecimal traffic = new java.math.BigDecimal("15.5");
                java.math.BigDecimal cost = new java.math.BigDecimal("20.00");

                doNothing().when(adminService).chargeClientForTraffic(1L, traffic, cost);

                mockMvc.perform(post("/api/admin/clients/1/charge")
                                .param("trafficGb", "15.5")
                                .param("cost", "20.00")
                                .with(user("admin").roles("ADMIN"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message")
                                                .value("Расход трафика начислен, баланс скорректирован"));

                verify(adminService, times(1)).chargeClientForTraffic(1L, traffic, cost);
        }
}
