package com.study_project.provider_api.command.impl;

import com.study_project.provider_api.command.PromotionCommand;
import com.study_project.provider_api.dao.PromotionDao;

public class DeletePromotionCommand implements PromotionCommand {
    private final PromotionDao promotionDao;
    private final Long promotionId;

    public DeletePromotionCommand(PromotionDao promotionDao, Long promotionId) {
        this.promotionDao = promotionDao;
        this.promotionId = promotionId;
    }

    @Override
    public void execute() {
        promotionDao.deleteById(promotionId);
    }
}
