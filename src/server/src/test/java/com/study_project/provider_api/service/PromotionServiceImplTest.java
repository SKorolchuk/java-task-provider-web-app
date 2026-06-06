package com.study_project.provider_api.service;

import com.study_project.provider_api.dao.PromotionDao;
import com.study_project.provider_api.model.Promotion;
import com.study_project.provider_api.service.impl.PromotionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Модульное тестирование бизнес-логики управления акциями (PromotionServiceImpl).
 * Проверяет корректность применения i18n правил валидации и интеграцию
 * с паттерном GoF "Команда" (Command).
 */
@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionDao promotionDao;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    @Test
    @DisplayName("Успешное объявление акции и привязка её ID к тарифу")
    void announcePromotion_Success() {
        Promotion promo = new Promotion();
        promo.setTitle("Летний Дев");
        promo.setDiscountPercentage(20);
        promo.setEndDate(LocalDate.now().plusMonths(1));

        when(promotionDao.save(promo)).thenReturn(42L);

        promotionService.announcePromotion(promo, 1L);

        verify(promotionDao, times(1)).save(promo);
        verify(promotionDao, times(1)).linkToTariff(1L, 42L);
    }

    @Test
    @DisplayName("Исключение, если скидка превышает 100 процентов")
    void announcePromotion_ThrowsException_WhenDiscountIsInvalid() {
        Promotion promo = new Promotion();
        promo.setDiscountPercentage(150);

        assertThrows(IllegalArgumentException.class, () -> {
            promotionService.announcePromotion(promo, 1L);
        });

        verify(promotionDao, never()).save(any(Promotion.class));
        verify(promotionDao, never()).linkToTariff(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Успешное получение полного списка акций администратором")
    void getAllPromotions_Success() {
        when(promotionDao.findAllForAdmin()).thenReturn(Collections.emptyList());

        List<Promotion> result = promotionService.getAllPromotions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(promotionDao, times(1)).findAllForAdmin();
    }

    @Test
    @DisplayName("Успешное выполнение UpdatePromotionCommand при обновлении параметров акции")
    void updatePromotion_Success() {
        Promotion promo = new Promotion();
        promo.setId(10L);
        promo.setTitle("Обновленная скидка");
        
        doNothing().when(promotionDao).update(promo);

        promotionService.updatePromotion(promo);

        verify(promotionDao, times(1)).update(promo);
    }

    @Test
    @DisplayName("Успешное выполнение DeletePromotionCommand при удалении акции администратором")
    void deletePromotion_Success() {
        Long targetPromoId = 5L;
        doNothing().when(promotionDao).deleteById(targetPromoId);

        promotionService.deletePromotion(targetPromoId);

        verify(promotionDao, times(1)).deleteById(targetPromoId);
    }
}
