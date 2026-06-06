package com.study_project.provider_api.controller;

import com.study_project.provider_api.dto.ActivePromotionDto;
import com.study_project.provider_api.dto.ClientDashboardDto;
import com.study_project.provider_api.dto.ClientTariffDetailsDto;
import com.study_project.provider_api.dto.ClientTrafficPageDto;
import com.study_project.provider_api.dto.RegisterRequest;
import com.study_project.provider_api.dto.UpdateProfileRequest;
import com.study_project.provider_api.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * REST-контроллер для обслуживания операций клиентов интернет-провайдера.
 * Предоставляет эндпоинты для открытой регистрации, управления балансом,
 * просмотра персонального профиля, пагинированной истории трафика,
 * смены тарифных планов (в том числе со скидками по маркетинговым акциям).
 */
@RestController
@RequestMapping("/api")
@Validated
public class ClientController {

    private final ClientService clientService;

    /**
     * Конструктор.
     * 
     * @param clientService сервис для обработки бизнес-логики клиента
     */
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Открытый эндпоинт для регистрации нового пользователя в системе.
     * Выполняет валидацию входных данных на уровне DTO перед передачей в сервис.
     * 
     * @param request DTO с регистрационными данными нового пользователя
     * @return ResponseEntity с JSON-сообщением об успешной регистрации
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody RegisterRequest request) {
        clientService.registerClient(request);
        return ResponseEntity.ok(Map.of("message", "Регистрация прошла успешно!"));
    }

    /**
     * Получение первоначальных статичных данных личного кабинета авторизованного
     * клиента.
     * Возвращает базовые метаданные профиля, списки доступных тарифов и акций.
     * 
     * @param principal объект текущего авторизованного пользователя
     * @return ResponseEntity, содержащий ClientDashboardDto с первичными данными ЛК
     */
    @GetMapping("/client/dashboard")
    public ResponseEntity<ClientDashboardDto> getDashboard(Principal principal) {
        return ResponseEntity.ok(clientService.getClientDashboard(principal.getName()));
    }

    /**
     * Пополнение баланса лицевого счета клиента.
     * 
     * @param amount    сумма пополнения (BigDecimal для точности биллинга)
     * @param principal объект текущего авторизованного пользователя
     * @return ResponseEntity с JSON-сообщением об успешном пополнении баланса
     */
    @PostMapping("/client/balance/deposit")
    public ResponseEntity<?> deposit(@RequestParam BigDecimal amount, Principal principal) {
        clientService.depositBalance(principal.getName(), amount);
        return ResponseEntity.ok(Map.of("message", "Баланс успешно пополнен"));
    }

    /**
     * Смена текущего тарифного плана клиента по стандартной стоимости.
     * 
     * @param tariffId  идентификатор (ID) подключаемого тарифа
     * @param principal объект текущего авторизованного пользователя
     * @return ResponseEntity с JSON-сообщением об успешном изменении тарифа
     */
    @PostMapping("/client/tariff/change")
    public ResponseEntity<?> changeTariff(@RequestParam Long tariffId, Principal principal) {
        clientService.changeTariff(principal.getName(), tariffId);
        return ResponseEntity.ok(Map.of("message", "Тарифный план изменен"));
    }

    /**
     * Модификация персональных личных параметров профиля клиента (email, пароль).
     * 
     * @param request   DTO с обновленными валидными параметрами профиля
     * @param principal объект текущего авторизованного пользователя
     * @return ResponseEntity с JSON-сообщением об успешном обновлении данных
     */
    @PutMapping("/client/profile/update")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
        clientService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Профиль успешно обновлен"));
    }

    /**
     * Возвращает список всех действующих и актуальных маркетинговых акций
     * провайдера.
     * Доступ разрешен авторизованным пользователям с ролью ROLE_CLIENT или
     * ROLE_ADMIN.
     * 
     * @return ResponseEntity, содержащий список строго типизированных объектов
     *         ActivePromotionDto
     */
    @GetMapping("/client/promotions")
    public ResponseEntity<List<ActivePromotionDto>> getActivePromotions() {
        List<ActivePromotionDto> activePromotions = clientService.getActivePromotions();
        return ResponseEntity.ok(activePromotions);
    }

    /**
     * Получение профиля клиента и технических параметров его текущего тарифа.
     * Метод оптимизирован для вызова один раз при инициализации личного кабинета.
     * 
     * @param principal объект текущего авторизованного пользователя
     * @return ResponseEntity, содержащий строго типизированный
     *         ClientTariffDetailsDto
     */
    @GetMapping("/client/profile")
    public ResponseEntity<ClientTariffDetailsDto> getProfile(Principal principal) {
        return ResponseEntity.ok(clientService.getClientProfile(principal.getName()));
    }

    /**
     * Контекстно-зависимый метод: постраничное получение истории потребления
     * трафика и финансов лицевого счета.
     * Минимизирует сетевой трафик (overfetching), не затрагивая статические данные
     * профиля.
     * 
     * @param principal объект текущего авторизованного пользователя
     * @param page      индекс запрашиваемой страницы истории (начиная с 0)
     * @param size      количество записей истории трафика на одну страницу
     * @return ResponseEntity, содержащий ClientTrafficPageDto с пагинированными
     *         списками
     */
    @GetMapping("/client/traffic")
    public ResponseEntity<ClientTrafficPageDto> getTraffic(
            Principal principal,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "3") @Min(1) int size) {

        return ResponseEntity.ok(clientService.getClientTrafficPage(principal.getName(), page, size));
    }

    /**
     * Смена тарифного плана клиента со списанием средств с учетом процентной скидки
     * по выбранной акции.
     * Выполняет атомарную транзакционную биллинговую операцию.
     * 
     * @param promotionId уникальный идентификатор (ID) активируемой маркетинговой
     *                    акции
     * @param principal   объект текущего авторизованного пользователя
     * @return ResponseEntity с JSON-сообщением об успешном изменении тарифа со
     *         скидкой
     */
    @PostMapping("/client/tariff/change-by-promo")
    public ResponseEntity<?> changeTariffByPromo(@RequestParam Long promotionId, Principal principal) {
        clientService.changeTariffWithPromotion(principal.getName(), promotionId);
        return ResponseEntity.ok(Map.of("message", "Тарифный план успешно изменен с учетом скидки по акции!"));
    }
}
