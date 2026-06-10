package com.study_project.provider_api.controller;

import com.study_project.provider_api.model.Promotion;
import com.study_project.provider_api.service.PromotionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционное тестирование REST-эндпоинтов управления акциями.
 * Проверяет разграничение прав (RBAC), атомарный вызов
 * методов
 * и валидность структуры возвращаемых списков акций.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromotionService promotionService;

    @Test
    @DisplayName("Успешное объявление акции администратором")
    void createPromotion_Success() throws Exception {
        String validPromoJson = "{\"title\": \"Скидка 50%\", \"discountPercentage\": 50, \"endDate\": \"2026-12-31\", \"description\": \"Тест\"}";

        doNothing().when(promotionService).announcePromotion(any(Promotion.class), eq(1L));

        mockMvc.perform(post("/api/admin/promotions")
                .param("targetTariffId", "1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPromoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Акция успешно объявлена для выбранного тарифа"));

        verify(promotionService, times(1)).announcePromotion(any(Promotion.class), eq(1L));
    }

    @Test
    @DisplayName("Отказ в доступе (403), если обычный клиент пытается создать акцию")
    void createPromotion_Forbidden_ForClient() throws Exception {
        String validPromoJson = "{\"title\": \"Скидка 50%\", \"discountPercentage\": 50, \"endDate\": \"2026-12-31\"}";

        mockMvc.perform(post("/api/admin/promotions")
                .param("targetTariffId", "1")
                .with(user("client").roles("CLIENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPromoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Успешное получение полного списка акций администратором")
    void getAllPromotionsForAdmin_Success() throws Exception {
        when(promotionService.getAllPromotions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/promotions")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(promotionService, times(1)).getAllPromotions();
    }

    @Test
    @DisplayName("Успешное обновление параметров акции администратором")
    void updatePromotion_Success() throws Exception {
        String validPromoJson = "{\"title\": \"Новое название\", \"discountPercentage\": 25, \"endDate\": \"2026-08-31\", \"description\": \"Обновлено\"}";

        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        mockMvc.perform(put("/api/admin/promotions/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPromoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Параметры акции успешно обновлены"));

        verify(promotionService, times(1)).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("Успешное принудительное удаление акции администратором")
    void deletePromotion_Success() throws Exception {
        doNothing().when(promotionService).deletePromotion(1L);

        mockMvc.perform(delete("/api/admin/promotions/1")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Акция успешно удалена из системы"));

        verify(promotionService, times(1)).deletePromotion(1L);
    }

    private Long eq(Long value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
