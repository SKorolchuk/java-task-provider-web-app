package com.study_project.provider_api.service;

import com.study_project.provider_api.dao.PromotionDao;
import com.study_project.provider_api.dao.TariffDao;
import com.study_project.provider_api.dao.UserDao;
import com.study_project.provider_api.dto.ActivePromotionDto;
import com.study_project.provider_api.dto.ClientTariffDetailsDto;
import com.study_project.provider_api.dto.RegisterRequest;
import com.study_project.provider_api.model.User;
import com.study_project.provider_api.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Класс для проверки бизнес-логики управления клиентами.
 * Тестирует правила валидации профилей, i18n ошибки и биллинг акций.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private TariffDao tariffDao;

    @Mock
    private PromotionDao promotionDao;

    @InjectMocks
    private ClientServiceImpl clientService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("Успешная регистрация нового клиента с хэшированием пароля через Builder")
    void registerClient_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("secure_password");
        request.setEmail("test@provider.com");

        when(userDao.findByUsername("test_user")).thenReturn(Optional.empty());

        clientService.registerClient(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("test_user", savedUser.getUsername());
        assertEquals("test@provider.com", savedUser.getEmail());
        assertEquals("CLIENT", savedUser.getRole());
        assertTrue(passwordEncoder.matches("secure_password", savedUser.getPassword()));
    }

    @Test
    @DisplayName("Выброс i18n исключения при регистрации, если логин уже занят")
    void registerClient_ThrowsException_WhenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing_user");
        request.setPassword("password123");
        request.setEmail("existing@provider.com");

        when(userDao.findByUsername("existing_user")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.registerClient(request);
        });

        assertEquals("user.exists", exception.getMessage());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Успешное получение изолированного профиля клиента")
    void getClientProfile_Success() {
        String username = "ivanov_ivan";
        User mockUser = new User.Builder().id(1L).username(username).isBlocked(false).build();
        ClientTariffDetailsDto mockDetails = new ClientTariffDetailsDto();
        mockDetails.setUsername(username);

        when(userDao.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(userDao.findClientWithTariffDetails(1L)).thenReturn(Optional.of(mockDetails));

        ClientTariffDetailsDto result = clientService.getClientProfile(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userDao, times(1)).findByUsername(username);
        verify(userDao, times(1)).findClientWithTariffDetails(1L);
    }

    @Test
    @DisplayName("Выброс исключения при попытке получить профиль заблокированного пользователя")
    void getClientProfile_ThrowsException_WhenUserIsBlocked() {
        String username = "blocked_user";
        User mockUser = new User.Builder().id(1L).username(username).isBlocked(true).build();

        when(userDao.findByUsername(username)).thenReturn(Optional.of(mockUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            clientService.getClientProfile(username);
        });

        assertEquals("Ваш аккаунт заблокирован", exception.getMessage());
        verify(userDao, never()).findClientWithTariffDetails(anyLong());
    }

    @Test
    @DisplayName("Успешный расчет точной стоимости BigDecimal при смене тарифа по акции")
    void changeTariffWithPromotion_Success() {
        String username = "rich_client";
        User mockUser = new User.Builder()
                .id(1L)
                .username(username)
                .balance(new BigDecimal("100.00")) // Достаточно средств
                .build();

        ActivePromotionDto mockPromo = new ActivePromotionDto();
        mockPromo.setTariffId(5L);
        mockPromo.setDiscountPercentage(20); // Скидка 20%
        mockPromo.setOriginalPrice(new BigDecimal("50.00")); // Оригинальная цена 50.00
        // Финальная цена со скидкой составит: 50 - (50 * 20 / 100) = 40.00 руб.

        when(userDao.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(promotionDao.findActivePromotionById(42L)).thenReturn(Optional.of(mockPromo));

        clientService.changeTariffWithPromotion(username, 42L);

        BigDecimal expectedFinalCharge = new BigDecimal("-40.00");
        verify(userDao, times(1)).updateBalance(1L, expectedFinalCharge);
        verify(userDao, times(1)).savePayment(1L, expectedFinalCharge);
        verify(userDao, times(1)).updateTariff(1L, 5L);
    }

    @Test
    @DisplayName("Выброс исключения, если на балансе клиента не хватает средств на тариф по акции")
    void changeTariffWithPromotion_ThrowsException_WhenInsufficientFunds() {
        String username = "poor_client";
        User mockUser = new User.Builder()
                .id(1L)
                .username(username)
                .balance(new BigDecimal("10.00")) // Мало средств
                .build();

        ActivePromotionDto mockPromo = new ActivePromotionDto();
        mockPromo.setTariffId(5L);
        mockPromo.setDiscountPercentage(10);
        mockPromo.setOriginalPrice(new BigDecimal("30.00")); // Цена со скидкой 10% = 27.00 руб.

        when(userDao.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(promotionDao.findActivePromotionById(100L)).thenReturn(Optional.of(mockPromo));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.changeTariffWithPromotion(username, 100L);
        });

        assertTrue(exception.getMessage().contains("Недостаточно средств"));
        verify(userDao, never()).updateBalance(anyLong(), any(BigDecimal.class));
        verify(userDao, never()).savePayment(anyLong(), any(BigDecimal.class));
    }
}
