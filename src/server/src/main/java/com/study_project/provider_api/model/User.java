package com.study_project.provider_api.model;

import java.math.BigDecimal;

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role; // CLIENT, ADMIN
    private BigDecimal balance;
    private boolean isBlocked;
    private Long tariffId;

    public User() {
    }

    // Приватный конструктор для Builder pattern
    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.role = builder.role;
        this.balance = builder.balance;
        this.isBlocked = builder.isBlocked;
        this.tariffId = builder.tariffId;
    }

    // Статический класс (Паттерн GoF)
    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String role = "CLIENT";
        private BigDecimal balance = BigDecimal.ZERO;
        private boolean isBlocked = false;
        private Long tariffId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder isBlocked(boolean isBlocked) {
            this.isBlocked = isBlocked;
            return this;
        }

        public Builder tariffId(Long tariffId) {
            this.tariffId = tariffId;
            return this;
        }

        public User build() {
            return new User(this);
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
}
