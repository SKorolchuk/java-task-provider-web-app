package com.study_project.provider_api.command;

/**
 * Интерфейс паттерна GoF "Команда" для выполнения административных
 * действий над маркетинговыми акциями.
 */
public interface PromotionCommand {
    void execute();
}
