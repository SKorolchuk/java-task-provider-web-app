package com.study_project.provider_api.command.impl;

import com.study_project.provider_api.command.PromotionCommand;
import com.study_project.provider_api.dao.PromotionDao;
import com.study_project.provider_api.model.Promotion;

public class UpdatePromotionCommand implements PromotionCommand {
    private final PromotionDao promotionDao;
    private final Promotion promotion;

    public UpdatePromotionCommand(PromotionDao promotionDao, Promotion promotion) {
        this.promotionDao = promotionDao;
        this.promotion = promotion;
    }

    @Override
    public void execute() {
        promotionDao.update(promotion);
    }
}
