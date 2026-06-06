package com.study_project.provider_api.service.impl;

import com.study_project.provider_api.command.PromotionCommand;
import com.study_project.provider_api.command.impl.DeletePromotionCommand;
import com.study_project.provider_api.command.impl.UpdatePromotionCommand;
import com.study_project.provider_api.dao.PromotionDao;
import com.study_project.provider_api.model.Promotion;
import com.study_project.provider_api.service.PromotionService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);
    private final PromotionDao promotionDao;

    public PromotionServiceImpl(PromotionDao promotionDao) {
        this.promotionDao = promotionDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getAllPromotions() {
        log.info("Запрос списка всех акций для панели администратора");
        return promotionDao.findAllForAdmin();
    }

    @Override
    @Transactional
    public void updatePromotion(Promotion promotion) {
        log.info("Администратор инициировал обновление акции ID: {}", promotion.getId());

        // Паттерн Команда
        PromotionCommand command = new UpdatePromotionCommand(promotionDao, promotion);
        command.execute();

        log.info("Акция ID {} успешно обновлена", promotion.getId());
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        log.warn("Администратор принудительно удаляет акцию ID: {}", id);

        PromotionCommand command = new DeletePromotionCommand(promotionDao, id);
        command.execute();

        log.info("Акция ID {} полностью удалена из системы", id);
    }

    @Override
    @Transactional
    public void announcePromotion(Promotion promotion, Long targetTariffId) {
        log.info("Администратор запускает процесс создания акции: '{}'", promotion.getTitle());

        if (promotion.getDiscountPercentage() < 0 || promotion.getDiscountPercentage() > 100) {
            log.error("Ошибка бизнес-логики: некорректный процент скидки ({})", promotion.getDiscountPercentage());
            throw new IllegalArgumentException("Процент скидки должен быть в диапазоне от 0 до 100");
        }

        Long generatedPromoId = promotionDao.save(promotion);
        log.debug("Акция успешно сохранена в таблице promotions с ID: {}", generatedPromoId);

        promotionDao.linkToTariff(targetTariffId, generatedPromoId);
        log.info("Акция ID {} успешно привязана к тарифному плану ID {}", generatedPromoId, targetTariffId);
    }
}
