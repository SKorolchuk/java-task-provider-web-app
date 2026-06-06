package com.study_project.provider_api.config;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Обработка ошибок валидации
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationErrors(ConstraintViolationException ex) {
        log.error("Ошибка серверной валидации аргументов: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Некорректные параметры запроса", "details", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, Locale locale) {
        // Проверяем, есть ли у нас перевод для текста этой ошибки (например, для
        // "user.exists")
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale);

        log.error("Бизнес-исключение уровня сервиса: {}", errorMessage);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        // Критические ошибки с stacktrace, чтобы их можно было отладить по логам
        log.error("КРИТИЧЕСКИЙ СИСТЕМНЫЙ СБОЙ: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error. Please contact logs."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        log.error("Попытка несанкционированного доступа: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleDtoValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        log.warn("Предупреждение валидации: входные параметры DTO не соответствуют правилам. Ошибки: {}", errors);

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation Failed",
                "details", errors));
    }
}
