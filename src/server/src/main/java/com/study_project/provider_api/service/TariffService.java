package com.study_project.provider_api.service;

import com.study_project.provider_api.model.Tariff;
import java.util.List;
import java.util.Map;

public interface TariffService {
    List<Tariff> getActiveTariffs();

    Map<String, Object> getTariffsPageForAdmin(int page, int size);

    void createTariff(Tariff tariff);

    void updateTariff(Long id, Tariff tariff);

    void toggleActiveStatus(Long id, boolean isActive);
}
