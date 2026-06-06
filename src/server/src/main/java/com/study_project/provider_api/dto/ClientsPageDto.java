package com.study_project.provider_api.dto;

import com.study_project.provider_api.model.User;
import java.util.List;

/**
 * DTO для представления пагинированной страницы клиентов.
 * Содержит массив данных пользователей и метаданные пагинации.
 */
public class ClientsPageDto {
    private List<User> clients;
    private int currentPage;
    private int totalItems;
    private int totalPages;

    public ClientsPageDto() {
    }

    public ClientsPageDto(List<User> clients, int currentPage, int totalItems, int totalPages) {
        this.clients = clients;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<User> getClients() {
        return clients;
    }

    public void setClients(List<User> clients) {
        this.clients = clients;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
