package com.study_project.provider_api.controller;

import com.study_project.provider_api.dto.ClientsPageDto;
import com.study_project.provider_api.service.AdminService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST-контроллер для выполнения административных операций.
 * Обеспечивает управление учетными записями клиентов, их блокировку,
 * просмотр списков с пагинацией и начисление расходов за интернет-трафик.
 * Доступ к эндпоинтам ограничен ролью ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private final AdminService adminService;

    /**
     * Конструктор.
     * 
     * @param adminService сервис для обработки бизнес-логики администратора
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Постраничный просмотр длинного списка клиентов.
     * Возвращает строго типизированный DTO объект с массивом пользователей и
     * метаданными.
     * 
     * @param page индекс запрашиваемой страницы (начиная с 0)
     * @param size количество отображаемых клиентов на одной странице
     * @return ResponseEntity, содержащий ClientsPageDto с данными пагинации
     */
    @GetMapping("/clients")
    public ResponseEntity<ClientsPageDto> getAllClients(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) int size) {

        return ResponseEntity.ok(adminService.getClientsPage(page, size));
    }

    /**
     * Изменение статуса блокировки клиента администратором.
     * Позволяет временно приостановить или возобновить предоставление услуг связи.
     * 
     * @param id    уникальный идентификатор (ID) клиента в базе данных
     * @param block флаг блокировки (true - заблокировать, false - разблокировать)
     * @return ResponseEntity с JSON-сообщением об успешном изменении статуса
     */
    @PatchMapping("/clients/{id}/block")
    public ResponseEntity<?> toggleBlockClient(
            @PathVariable Long id,
            @RequestParam boolean block) {

        adminService.changeBlockStatus(id, block);
        return ResponseEntity.ok(Map.of("message", "Статус блокировки успешно изменен"));
    }

    /**
     * Начисление расхода трафика клиенту и автоматическое списание средств за него.
     * Делегирует всю бизнес-логику в сервисный слой AdminService, обеспечивая
     * транзакционность.
     * 
     * @param id        уникальный идентификатор (ID) клиента в базе данных
     * @param trafficGb объем потребленного интернет-трафика в гигабайтах (GB)
     * @param cost      сумма денежных средств к списанию с баланса за данный трафик
     * @return ResponseEntity с JSON-сообщением об успешном списании и фиксации
     *         расхода
     */
    @PostMapping("/clients/{id}/charge")
    public ResponseEntity<?> chargeClient(
            @PathVariable Long id,
            @RequestParam BigDecimal trafficGb,
            @RequestParam BigDecimal cost) {

        adminService.chargeClientForTraffic(id, trafficGb, cost);

        return ResponseEntity.ok(Map.of(
                "message", "Расход трафика начислен, баланс скорректирован"));
    }
}
