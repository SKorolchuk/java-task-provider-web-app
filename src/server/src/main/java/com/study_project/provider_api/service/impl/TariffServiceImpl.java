package com.study_project.provider_api.service.impl;

import com.study_project.provider_api.dao.TariffDao;
import com.study_project.provider_api.model.Tariff;
import com.study_project.provider_api.service.TariffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TariffServiceImpl implements TariffService {

    private static final Logger log = LoggerFactory.getLogger(TariffServiceImpl.class);
    private final TariffDao tariffDao;

    public TariffServiceImpl(TariffDao tariffDao) {
        this.tariffDao = tariffDao;
    }

    @Override
    public List<Tariff> getActiveTariffs() {
        log.info("Запрос списка активных тарифов для клиентов");
        return tariffDao.findAllActive();
    }

    @Override
    public Map<String, Object> getTariffsPageForAdmin(int page, int size) {
        log.info("Администратор запросил страницу тарифов. Страница: {}, Размер: {}", page, size);
        int offset = page * size;

        List<Tariff> tariffs = tariffDao.findAllWithPagination(size, offset);
        int totalItems = tariffDao.countAll();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        Map<String, Object> response = new HashMap<>();
        response.put("tariffs", tariffs);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        return response;
    }

    @Override
    public void createTariff(Tariff tariff) {
        log.info("Попытка создания нового тарифа администратором: {}", tariff.getName());
        if (tariff.getPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("Цена тарифа не может быть отрицательной");
        }
        tariffDao.save(tariff);
    }

    @Override
    public void updateTariff(Long id, Tariff tariff) {
        log.warn("Попытка обновления тарифа с ID: {}", id);
        tariffDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Тариф не найден"));

        tariff.setId(id);
        tariffDao.update(tariff);
        log.info("Тариф с ID {} успешно обновлен", id);
    }

    @Override
    public void toggleActiveStatus(Long id, boolean isActive) {
        log.warn("Попытка обновления статуса тарифа с ID: {}", id);
        tariffDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Тариф не найден"));

        tariffDao.toggleActiveStatus(id, isActive);

        log.info("Статус тарифа с ID {} успешно обновлен", id);
    }
}
