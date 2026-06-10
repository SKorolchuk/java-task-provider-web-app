package com.study_project.provider_api.dao;

import com.study_project.provider_api.model.Tariff;
import java.util.List;
import java.util.Optional;

public interface TariffDao {
    Optional<Tariff> findById(Long id);

    List<Tariff> findAllActive();

    List<Tariff> findAllWithPagination(int limit, int offset);

    int countAll();

    void save(Tariff tariff);

    void update(Tariff tariff);

    void toggleActiveStatus(Long id, boolean isActive);
}
