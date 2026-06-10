package com.study_project.provider_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST-контроллер для управления процессами аутентификации и авторизации
 * пользователей.
 * Обеспечивает безопасный вход в систему (Sign In), интеграцию с подсистемой
 * Spring Security, шифрование сессий и поддержку мультиязычных сообщений об
 * ошибках.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;

    /**
     * Конструктор.
     * 
     * @param authenticationManager менеджер для верификации учетных данных
     *                              пользователей
     * @param messageSource         компонент для извлечения
     *                              интернационализированных строк
     */
    public AuthController(AuthenticationManager authenticationManager, MessageSource messageSource) {
        this.authenticationManager = authenticationManager;
        this.messageSource = messageSource;
    }

    /**
     * Выполняет аутентификацию пользователя на основе переданного логина и пароля.
     * При успешной проверке инициализирует защищенную HTTP-сессию и сохраняет
     * контекст
     * безопасности Spring Security, предотвращая его сброс при последующих
     * запросах.
     * В случае неверных учетных данных возвращает локализованное сообщение об
     * ошибке
     * согласно заголовку Accept-Language.
     * 
     * @param credentials карта параметров тела запроса, содержащая ключи "username"
     *                    и "password"
     * @param request     объект HTTP-запроса для принудительного создания и
     *                    управления сессией
     * @return ResponseEntity с метаданными пользователя (имя, роли) при успешном
     *         входе (200 OK)
     *         или объект ошибки при неверных данных (401 Unauthorized)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        log.info("Получен запрос на аутентификацию для пользователя: {}", username);

        try {
            // 1. Формируем токен аутентификации на основе сырых данных формы входа
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            // 2. Делегируем проверку пароля по базе данных. Вызывает цепочку хэширования
            // BCrypt
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 3. Фиксируем успешную проверку полномочий в контексте текущего потока
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 4. Принудительно связываем контекст безопасности со стандартной HTTP-сессией
            // (JSESSIONID)
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            log.info("Пользователь {} успешно вошел в систему", username);

            return ResponseEntity.ok(Map.of(
                    "message", "Success",
                    "username", username,
                    "roles", authentication.getAuthorities().stream()
                            .map(auth -> auth.getAuthority())
                            .toList()));

        } catch (Exception e) {
            log.error("Ошибка аутентификации для пользователя {}: {}", username, e.getMessage());

            // Динамически извлекаем перевод ошибки на основе текущей языковой локали
            // запроса
            String localizedError = messageSource.getMessage(
                    "auth.bad.credentials",
                    null,
                    LocaleContextHolder.getLocale());

            // Защищаем от утечки на сторону клиента
            return ResponseEntity.status(401).body(Map.of("error", localizedError));
        }
    }
}
