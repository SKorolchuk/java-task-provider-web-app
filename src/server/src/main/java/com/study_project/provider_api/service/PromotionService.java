package com.study_project.provider_api.service;

import java.util.List;

import com.study_project.provider_api.model.Promotion;

public interface PromotionService {
    void announcePromotion(Promotion promotion, Long targetTariffId);

    List<Promotion> getAllPromotions();

    void updatePromotion(Promotion promotion);

    void deletePromotion(Long id);
}
