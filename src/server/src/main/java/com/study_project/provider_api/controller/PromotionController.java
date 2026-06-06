package com.study_project.provider_api.controller;

import com.study_project.provider_api.model.Promotion;
import com.study_project.provider_api.service.PromotionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST-контроллер для управления маркетинговыми акциями и скидками.
 * Предоставляет полноценный CRUD-интерфейс, инкапсулирующий административные
 * действия
 * посредством паттерна проектирования GoF "Команда" (Command).
 * Доступ к эндпоинтам ограничен пользователями с правами Администратора
 * (ADMIN).
 */
@RestController
@RequestMapping("/api/admin/promotions")
@Validated
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * Конструктор.
     * 
     * @param promotionService сервисный слой для управления бизнес-логикой
     *                         маркетинговых акций
     */
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Создает новую маркетинговую акцию и принудительно привязывает её к выбранному
     * тарифу.
     * Реализует атомарную транзакционную логику связывания сущностей
     * "многие-ко-многим".
     * Входной JSON автоматически очищается глобальным XssFilter для предотвращения
     * XSS-уязвимостей.
     * 
     * @param promo          объект сохраняемой акции, проходящий серверную
     *                       валидацию DTO параметров
     * @param targetTariffId уникальный идентификатор тарифа, на который
     *                       накладывается процентная скидка
     * @return ResponseEntity с JSON-подтверждением успешного объявления акции (200
     *         OK)
     */
    @PostMapping
    public ResponseEntity<?> createPromotion(
            @Valid @RequestBody Promotion promo,
            @RequestParam @Min(1) Long targetTariffId) {

        promotionService.announcePromotion(promo, targetTariffId);

        return ResponseEntity.ok(Map.of(
                "message", "Акция успешно объявлена для выбранного тарифа"));
    }

    /**
     * Получение полного списка всех акций в системе для панели администратора.
     * Выгружает данные по неактивным, архивным и действующим маркетинговым
     * предложениям.
     * 
     * @return ResponseEntity, содержащий упорядоченный список (List) сущностей
     *         Promotion
     */
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotionsForAdmin() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    /**
     * Модификация параметров существующей в системе маркетинговой акции.
     * Инкапсулирует операцию изменения данных с помощью команды
     * UpdatePromotionCommand.
     * 
     * @param id    уникальный идентификатор (ID) редактируемой акции в базе данных
     * @param promo объект акции с обновленными параметрами, проходящий
     *              валидацию @Valid
     * @return ResponseEntity с JSON-сообщением об успешном обновлении характеристик
     *         акции
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody Promotion promo) {

        promo.setId(id);
        promotionService.updatePromotion(promo);
        return ResponseEntity.ok(Map.of("message", "Параметры акции успешно обновлены"));
    }

    /**
     * Полное принудительное удаление маркетинговой акции из системы.
     * Инкапсулирует операцию удаления на уровне СУБД с помощью команды
     * DeletePromotionCommand.
     * Каскадно удаляет зависимости из связующей таблицы tariff_promotions благодаря
     * FOREIGN KEY.
     * 
     * @param id уникальный идентификатор (ID) удаляемой акции
     * @return ResponseEntity с JSON-сообщением об успешном удалении записи из СУБД
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(Map.of("message", "Акция успешно удалена из системы"));
    }
}
