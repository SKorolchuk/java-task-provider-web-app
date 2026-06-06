package com.study_project.provider_api.controller;

import com.study_project.provider_api.model.Tariff;
import com.study_project.provider_api.service.TariffService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционное тестирование REST-эндпоинтов управления тарифными планами.
 * Проверяет разграничение ролевых прав (RBAC) между клиентами и админами,
 * серверную валидацию числовых диапазонов пагинации и атомарные CRUD-действия.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TariffService tariffService;

    @Test
    @DisplayName("Успешное получение списка активных тарифов клиентом")
    void getActiveTariffs_Success() throws Exception {
        when(tariffService.getActiveTariffs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/client/tariffs")
                .with(user("client_user").roles("CLIENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(tariffService, times(1)).getActiveTariffs();
    }

    @Test
    @DisplayName("Успешное получение списка тарифов администратором с пагинацией")
    void getAllTariffsForAdmin_Success() throws Exception {
        Map<String, Object> mockPage = Map.of("tariffs", Collections.emptyList(), "totalPages", 0);
        when(tariffService.getTariffsPageForAdmin(0, 5)).thenReturn(mockPage);

        mockMvc.perform(get("/api/admin/tariffs")
                .param("page", "0")
                .param("size", "5")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffs").isArray());

        verify(tariffService, times(1)).getTariffsPageForAdmin(0, 5);
    }

    @Test
    @DisplayName("Ошибка 400 Bad Request, если передан невалидный размер страницы (size=0)")
    void getAllTariffsForAdmin_BadRequest_WhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/api/admin/tariffs")
                .param("page", "0")
                .param("size", "0")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешное создание нового тарифного плана администратором")
    void createTariff_Success() throws Exception {
        String validTariffJson = "{\"name\": \"Турбо 100\", \"price\": 450.00, \"speedMbps\": 100, \"description\": \"Тест\", \"active\": true}";

        doNothing().when(tariffService).createTariff(any(Tariff.class));

        mockMvc.perform(post("/api/admin/tariffs")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validTariffJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Тарифный план успешно создан"));

        verify(tariffService, times(1)).createTariff(any(Tariff.class));
    }

    @Test
    @DisplayName("Успешная модификация и корректировка параметров тарифа администратором")
    void updateTariff_Success() throws Exception {
        String validTariffJson = "{\"name\": \"Обновленный\", \"price\": 500.00, \"speedMbps\": 200, \"description\": \"Изменено\"}";

        doNothing().when(tariffService).updateTariff(eq(1L), any(Tariff.class));

        mockMvc.perform(put("/api/admin/tariffs/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validTariffJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Тарифный план обновлен"));

        verify(tariffService, times(1)).updateTariff(eq(1L), any(Tariff.class));
    }

    @Test
    @DisplayName("Успешная архивация тарифного плана администратором (soft delete)")
    void toggleTariffStatus_Success() throws Exception {
        doNothing().when(tariffService).toggleActiveStatus(1L, false);

        mockMvc.perform(patch("/api/admin/tariffs/1/toggle")
                .param("active", "false")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Статус активности тарифа успешно изменен"));

        verify(tariffService, times(1)).toggleActiveStatus(1L, false);
    }

    @Test
    @DisplayName("Отказ в доступе (403) при попытке архивации тарифа под ролью CLIENT")
    void toggleTariffStatus_Forbidden_ForClient() throws Exception {
        mockMvc.perform(patch("/api/admin/tariffs/1/toggle")
                .param("active", "false")
                .with(user("client_user").roles("CLIENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
