package com.study_project.provider_api.service;

import com.study_project.provider_api.dao.UserDao;
import com.study_project.provider_api.dto.ClientsPageDto;
import com.study_project.provider_api.model.User;
import com.study_project.provider_api.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Модульное тестирование бизнес-логики администратора (AdminServiceImpl).
 * Изолирует сервис от базы данных с помощью Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("Успешный расчет лимитов и пагинации списка клиентов")
    void getClientsPage_Success() {
        User mockUser = new User.Builder()
                .id(1L)
                .username("ivanov_ivan")
                .role("CLIENT")
                .build();

        when(userDao.findAllClientsWithPagination(5, 0)).thenReturn(List.of(mockUser));
        when(userDao.countAllClients()).thenReturn(25);

        ClientsPageDto result = adminService.getClientsPage(0, 5);

        assertNotNull(result);
        assertEquals(0, result.getCurrentPage());
        assertEquals(25, result.getTotalItems());
        assertEquals(5, result.getTotalPages());
        assertFalse(result.getClients().isEmpty());
        assertEquals("ivanov_ivan", result.getClients().get(0).getUsername());

        verify(userDao, times(1)).findAllClientsWithPagination(5, 0);
        verify(userDao, times(1)).countAllClients();
    }

    @Test
    @DisplayName("Исключение, если администратор пытается изменить статус несуществующего ID")
    void changeBlockStatus_ThrowsException_WhenUserNotFound() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.changeBlockStatus(999L, true);
        });

        verify(userDao, times(1)).findById(999L);
        verify(userDao, never()).updateBlockedStatus(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Успешное начисление расхода трафика и списания средств с баланса клиента")
    void chargeClientForTraffic_Success() {
        Long userId = 1L;
        BigDecimal trafficGb = new BigDecimal("15.5");
        BigDecimal cost = new BigDecimal("20.00");
        User mockUser = new User.Builder().id(userId).username("ivanov_ivan").build();

        when(userDao.findById(userId)).thenReturn(Optional.of(mockUser));
        doNothing().when(userDao).recordTrafficAndCharge(userId, trafficGb, cost);

        adminService.chargeClientForTraffic(userId, trafficGb, cost);

        verify(userDao, times(1)).findById(userId);
        verify(userDao, times(1)).recordTrafficAndCharge(userId, trafficGb, cost);
    }

    @Test
    @DisplayName("Исключение при начислении трафика, если клиент не найден в системе")
    void chargeClientForTraffic_ThrowsException_WhenUserNotFound() {
        Long userId = 999L;
        BigDecimal trafficGb = new BigDecimal("10.0");
        BigDecimal cost = new BigDecimal("15.00");

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.chargeClientForTraffic(userId, trafficGb, cost);
        });

        verify(userDao, times(1)).findById(userId);
        verify(userDao, never()).recordTrafficAndCharge(anyLong(), any(BigDecimal.class), any(BigDecimal.class));
    }
}
