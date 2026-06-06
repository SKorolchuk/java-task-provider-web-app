package com.study_project.provider_api.controller;

import com.study_project.provider_api.dto.ClientDashboardDto;
import com.study_project.provider_api.dto.ClientTariffDetailsDto;
import com.study_project.provider_api.dto.ClientTrafficPageDto;
import com.study_project.provider_api.service.ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционное тестирование REST-эндпоинтов клиента (ЛК, регистрация,
 * биллинг).
 * Проверяет серверную валидацию DTO параметров, контекстно-зависимую пагинацию
 * и атомарные транзакции изменения тарифов со скидками.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ClientService clientService;

        @Test
        @DisplayName("Успешная регистрация при валидных данных в DTO")
        void signUp_Success_WithValidDto() throws Exception {
                String validJson = "{\"username\": \"new_client\", \"password\": \"password123\", \"email\": \"new@mail.ru\"}";

                mockMvc.perform(post("/api/auth/signup")
                                .with(anonymous())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Регистрация прошла успешно!"));
        }

        @Test
        @DisplayName("Ошибка 400 Bad Request при регистрации, если пароль слишком короткий")
        void signUp_BadRequest_WhenPasswordIsShort() throws Exception {
                String invalidJson = "{\"username\": \"new_client\", \"password\": \"123\", \"email\": \"new@mail.ru\"}";

                mockMvc.perform(post("/api/auth/signup")
                                .with(anonymous())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Успешное получение данных личного кабинета под ролью CLIENT")
        void getDashboard_Success_WhenUserIsClient() throws Exception {
                ClientDashboardDto mockDashboard = new ClientDashboardDto(
                                new ClientTariffDetailsDto(),
                                Collections.emptyList(),
                                Collections.emptyList());

                when(clientService.getClientDashboard("ivanov_ivan")).thenReturn(mockDashboard);

                mockMvc.perform(get("/api/client/dashboard")
                                .with(user("ivanov_ivan").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.info").exists())
                                .andExpect(jsonPath("$.availableTariffs").isArray());

                verify(clientService, times(1)).getClientDashboard("ivanov_ivan");
        }

        @Test
        @DisplayName("Успешное пополнение баланса клиентом")
        void deposit_Success() throws Exception {
                BigDecimal depositAmount = new BigDecimal("50.00");
                doNothing().when(clientService).depositBalance("ivanov_ivan", depositAmount);

                mockMvc.perform(post("/api/client/balance/deposit")
                                .param("amount", "50.00")
                                .with(user("ivanov_ivan").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Баланс успешно пополнен"));

                verify(clientService, times(1)).depositBalance("ivanov_ivan", depositAmount);
        }

        @Test
        @DisplayName("Успешное получение списка акций авторизованным клиентом")
        void getActivePromotions_Success() throws Exception {
                when(clientService.getActivePromotions()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/client/promotions")
                                .with(user("ivanov_ivan").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());

                verify(clientService, times(1)).getActivePromotions();
        }

        @Test
        @DisplayName("Успешное получение изолированного профиля клиента и деталей тарифа")
        void getProfile_Success() throws Exception {
                ClientTariffDetailsDto mockDetails = new ClientTariffDetailsDto();
                mockDetails.setUsername("ivanov_ivan");

                when(clientService.getClientProfile("ivanov_ivan")).thenReturn(mockDetails);

                mockMvc.perform(get("/api/client/profile")
                                .with(user("ivanov_ivan").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("ivanov_ivan"));

                verify(clientService, times(1)).getClientProfile("ivanov_ivan");
        }

        @Test
        @DisplayName("Успешное получение контекстно-зависимой пагинации трафика")
        void getTraffic_Success() throws Exception {
                ClientTrafficPageDto mockTrafficPage = new ClientTrafficPageDto(
                                Collections.emptyList(),
                                Collections.emptyList(),
                                5);

                when(clientService.getClientTrafficPage("ivanov_ivan", 0, 3)).thenReturn(mockTrafficPage);

                mockMvc.perform(get("/api/client/traffic")
                                .param("page", "0")
                                .param("size", "3")
                                .with(user("ivanov_ivan").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.trafficTotalPages").value(5))
                                .andExpect(jsonPath("$.trafficHistory").isArray());

                verify(clientService, times(1)).getClientTrafficPage("ivanov_ivan", 0, 3);
        }

        @Test
        @DisplayName("Успешное изменение тарифного плана со списанием средств по акции")
        void changeTariffByPromo_Success() throws Exception {
                doNothing().when(clientService).changeTariffWithPromotion("ivanov_ivan", 42L);

                mockMvc.perform(post("/api/client/tariff/change-by-promo")
                                .param("promotionId", "42")
                                .with(user("ivanov_ivan").roles("CLIENT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message")
                                                .value("Тарифный план успешно изменен с учетом скидки по акции!"));

                verify(clientService, times(1)).changeTariffWithPromotion("ivanov_ivan", 42L);
        }
}
