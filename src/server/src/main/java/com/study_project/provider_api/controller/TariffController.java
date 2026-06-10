package com.study_project.provider_api.controller;

import com.study_project.provider_api.model.Tariff;
import com.study_project.provider_api.service.TariffService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST-контроллер для управления тарифными планами интернет-провайдера.
 * Предоставляет методы для чтения активных планов клиентами, а также
 * постраничный вывод,
 * добавление, корректировку и архивацию тарифов администраторами (ADMIN).
 */
@RestController
@RequestMapping("/api")
@Validated
public class TariffController {

    private final TariffService tariffService;

    /**
     * Конструктор.
     * 
     * @param tariffService сервисный слой для обработки бизнес-логики тарифных
     *                      планов
     */
    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    /**
     * Возвращает список только активных тарифов для клиентов.
     * Используется на фронтенде (в личном кабинете) для вывода доступных вариантов
     * подключения.
     * 
     * @return ResponseEntity со списком (List) сущностей Tariff, у которых флаг
     *         is_active равен TRUE
     */
    @GetMapping("/client/tariffs")
    public ResponseEntity<?> getActiveTariffs() {
        return ResponseEntity.ok(tariffService.getActiveTariffs());
    }

    /**
     * Постраничный вывод ВСЕХ тарифных планов (включая архивные) для панели
     * администратора.
     * Оптимизирует нагрузку на СУБД с помощью SQL-пагинации (LIMIT и OFFSET).
     * 
     * @param page индекс запрашиваемой страницы истории (начиная с 0)
     * @param size количество тарифных планов, отображаемых на одной странице
     * @return ResponseEntity с картой метаданных (список тарифов, общее количество
     *         страниц и элементов)
     */
    @GetMapping("/admin/tariffs")
    public ResponseEntity<?> getAllTariffsForAdmin(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return ResponseEntity.ok(tariffService.getTariffsPageForAdmin(page, size));
    }

    /**
     * Добавление нового тарифного плана в систему провайдера.
     * Доступно исключительно пользователям с правами Администратора (ADMIN).
     * 
     * @param tariff объект создаваемого тарифа, содержащий имя, цену, скорость и
     *               описание
     * @return ResponseEntity с JSON-сообщением об успешном сохранении нового
     *         тарифного плана
     */
    @PostMapping("/admin/tariffs")
    public ResponseEntity<?> createTariff(@RequestBody Tariff tariff) {
        tariffService.createTariff(tariff);
        return ResponseEntity.ok(Map.of("message", "Тарифный план успешно создан"));
    }

    /**
     * Модификация и корректировка характеристик существующего тарифного плана.
     * Позволяет администратору изменять стоимость, скорость или описание тарифа.
     * 
     * @param id     уникальный идентификатор (ID) редактируемого тарифа в базе
     *               данных
     * @param tariff объект тарифа с обновленными параметрами
     * @return ResponseEntity с JSON-сообщением об успешном обновлении данных тарифа
     */
    @PutMapping("/admin/tariffs/{id}")
    public ResponseEntity<?> updateTariff(@PathVariable Long id, @RequestBody Tariff tariff) {
        tariffService.updateTariff(id, tariff);
        return ResponseEntity.ok(Map.of("message", "Тарифный план обновлен"));
    }

    /**
     * Переключение статуса активности тарифа (Архивация / Извлечение из архива).
     * Вместо физического удаления используется логическое (soft delete) через флаг
     * active,
     * что предотвращает нарушение целостности данных у клиентов, которые уже
     * подключены к нему.
     * 
     * @param id     уникальный идентификатор (ID) тарифа в базе данных
     * @param active целевой статус активности (true - сделать активным, false -
     *               отправить в архив)
     * @return ResponseEntity с JSON-сообщением об успешном изменении статуса тарифа
     */
    @PatchMapping("/admin/tariffs/{id}/toggle")
    public ResponseEntity<?> toggleTariffStatus(@PathVariable Long id, @RequestParam boolean active) {
        tariffService.toggleActiveStatus(id, active);
        return ResponseEntity.ok(Map.of("message", "Статус активности тарифа успешно изменен"));
    }
}
