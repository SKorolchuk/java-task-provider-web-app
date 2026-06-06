package com.study_project.provider_api.service;

import com.study_project.provider_api.dao.TariffDao;
import com.study_project.provider_api.model.Tariff;
import com.study_project.provider_api.service.impl.TariffServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Модульное тестирование бизнес-логики управления тарифами (TariffServiceImpl).
 * Изолирует тестируемый сервис от базы данных с помощью Mockito.
 */
@ExtendWith(MockitoExtension.class)
class TariffServiceImplTest {

    @Mock
    private TariffDao tariffDao;

    @InjectMocks
    private TariffServiceImpl tariffService;

    @Test
    @DisplayName("Успешное создание тарифного плана с валидными параметрами")
    void createTariff_Success() {
        Tariff validTariff = new Tariff();
        validTariff.setName("Гигабит Плюс");
        validTariff.setPrice(new BigDecimal("450.00"));
        validTariff.setSpeedMbps(1000);
        validTariff.setActive(true);

        doNothing().when(tariffDao).save(validTariff);

        tariffService.createTariff(validTariff);

        verify(tariffDao, times(1)).save(validTariff);
    }

    @Test
    @DisplayName("Исключение при создании тарифа с отрицательной стоимостью")
    void createTariff_ThrowsException_WhenPriceIsNegative() {
        Tariff invalidTariff = new Tariff();
        invalidTariff.setName("Битое Окружение");
        invalidTariff.setPrice(new BigDecimal("-15.00"));

        assertThrows(IllegalArgumentException.class, () -> {
            tariffService.createTariff(invalidTariff);
        });

        verify(tariffDao, never()).save(any(Tariff.class));
    }

    @Test
    @DisplayName("Успешное получение списка только активных тарифов для клиентов")
    void getActiveTariffs_Success() {
        when(tariffDao.findAllActive()).thenReturn(Collections.emptyList());

        List<Tariff> result = tariffService.getActiveTariffs();

        assertNotNull(result);
        verify(tariffDao, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Успешный расчет пагинации и страниц при запросе всех тарифов администратором")
    void getTariffsPageForAdmin_Success() {
        int page = 1;
        int size = 5;
        int offset = page * size; // 5

        when(tariffDao.findAllWithPagination(size, offset)).thenReturn(Collections.emptyList());
        when(tariffDao.countAll()).thenReturn(22); // 22 тарифа всего в базе

        Map<String, Object> result = tariffService.getTariffsPageForAdmin(page, size);

        assertNotNull(result);
        assertEquals(page, result.get("currentPage"));
        assertEquals(22, result.get("totalItems"));
        assertEquals(5, result.get("totalPages")); // Математика: 22 тарифа / 5 на страницу = 4.4 -> округление вверх до 5 страниц
        
        verify(tariffDao, times(1)).findAllWithPagination(size, offset);
        verify(tariffDao, times(1)).countAll();
    }
}
