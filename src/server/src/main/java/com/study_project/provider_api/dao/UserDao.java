package com.study_project.provider_api.dao;

import com.study_project.provider_api.dto.ClientTariffDetailsDto;
import com.study_project.provider_api.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAllClientsWithPagination(int limit, int offset);

    int countAllClients();

    void updateBlockedStatus(Long userId, boolean isBlocked);

    void updateBalance(Long userId, BigDecimal amount);

    void save(User user);

    void updatePersonalParams(Long userId, String email, String password);

    void updateTariff(Long userId, Long tariffId);

    List<Map<String, Object>> findTrafficStatsByUserId(Long userId, int limit, int offset);

    int countTrafficStatsByUserId(Long userId);

    List<Map<String, Object>> findPaymentsByUserId(Long userId);

    void savePayment(Long userId, BigDecimal amount);

    Optional<ClientTariffDetailsDto> findClientWithTariffDetails(Long userId);

    void recordTrafficAndCharge(Long userId, BigDecimal trafficGb, BigDecimal cost);
}
