package com.study_project.provider_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "{user.username.blank}")
    @Size(min = 4, max = 50, message = "{user.username.size}")
    private String username;

    @NotBlank(message = "{user.password.blank}")
    @Size(min = 6, message = "{user.password.size}")
    private String password;

    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.invalid}")
    private String email;

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
}
