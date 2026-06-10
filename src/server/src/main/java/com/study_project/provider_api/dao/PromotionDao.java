package com.study_project.provider_api.dao;

import java.util.List;
import java.util.Optional;

import com.study_project.provider_api.dto.ActivePromotionDto;
import com.study_project.provider_api.model.Promotion;

public interface PromotionDao {
    Long save(Promotion promotion);

    void linkToTariff(Long tariffId, Long promotionId);

    List<ActivePromotionDto> findAllActiveWithTariffName();

    List<Promotion> findAllForAdmin();

    void update(Promotion promotion);

    void deleteById(Long id);

    Optional<ActivePromotionDto> findActivePromotionById(Long promotionId);
}
