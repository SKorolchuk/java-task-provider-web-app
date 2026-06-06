package com.study_project.provider_api.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) для представления детальной информации о клиенте
 * вместе с техническими и финансовыми параметрами его текущего тарифа.
 * Обеспечивает строгую типизацию данных при передаче через REST API.
 */
public class ClientTariffDetailsDto {

    // Поля из таблицы пользователей (users)
    private Long id;
    private String username;
    private String email;
    private BigDecimal balance;
    private boolean isBlocked;
    private Long tariffId;

    // Поля, подтягиваемые через LEFT JOIN из таблицы тарифов (tariffs)
    private String tariffName;
    private BigDecimal tariffPrice;
    private Integer speedMbps;

    public ClientTariffDetailsDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public BigDecimal getTariffPrice() {
        return tariffPrice;
    }

    public void setTariffPrice(BigDecimal tariffPrice) {
        this.tariffPrice = tariffPrice;
    }

    public Integer getSpeedMbps() {
        return speedMbps;
    }

    public void setSpeedMbps(Integer speedMbps) {
        this.speedMbps = speedMbps;
    }
}
