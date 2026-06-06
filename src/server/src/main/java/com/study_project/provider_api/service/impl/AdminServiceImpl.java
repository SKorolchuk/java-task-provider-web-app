package com.study_project.provider_api.service.impl;

import com.study_project.provider_api.dao.UserDao;
import com.study_project.provider_api.dto.ClientsPageDto;
import com.study_project.provider_api.model.User;
import com.study_project.provider_api.service.AdminService;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    // Логгер SLF4J (встроенный в Web-стартер)
    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserDao userDao;

    public AdminServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientsPageDto getClientsPage(int page, int size) {
        log.info("Запрос страницы клиентов администратором. Страница: {}, Размер: {}", page, size);

        int offset = page * size;

        java.util.List<User> clients = userDao.findAllClientsWithPagination(size, offset);
        int totalClients = userDao.countAllClients();
        int totalPages = (int) Math.ceil((double) totalClients / size);

        return new ClientsPageDto(
                clients,
                page,
                totalClients,
                totalPages);
    }

    @Override
    public void changeBlockStatus(Long userId, boolean block) {
        log.warn("Изменение статуса блокировки для пользователя ID: {}. Новый статус: {}", userId, block);

        userDao.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с ID {} не найден для блокировки/разблокировки", userId);
            return new IllegalArgumentException("Пользователь не найден");
        });

        userDao.updateBlockedStatus(userId, block);
    }

    @Override
    @Transactional
    public void chargeClientForTraffic(Long userId, BigDecimal trafficGb, BigDecimal cost) {
        log.info(
                "Администратор начисляет расход трафика для пользователя ID: {}. Трафик: {} GB, Сумма к списанию: {} руб.",
                userId, trafficGb, cost);

        userDao.findById(userId).orElseThrow(() -> {
            log.error("Ошибка начисления: клиент с ID {} не найден в системе", userId);
            return new IllegalArgumentException("Пользователь не найден");
        });

        userDao.recordTrafficAndCharge(userId, trafficGb, cost);

        log.info("Расход трафика успешно зафиксирован для пользователя ID: {}", userId);
    }
}
