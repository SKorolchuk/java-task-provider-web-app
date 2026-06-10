package com.study_project.provider_api.service;

import com.study_project.provider_api.dto.ActivePromotionDto;
import com.study_project.provider_api.dto.ClientDashboardDto;
import com.study_project.provider_api.dto.ClientTariffDetailsDto;
import com.study_project.provider_api.dto.ClientTrafficPageDto;
import com.study_project.provider_api.dto.RegisterRequest;
import com.study_project.provider_api.dto.UpdateProfileRequest;
import java.math.BigDecimal;
import java.util.List;

public interface ClientService {
    void registerClient(RegisterRequest request);

    void depositBalance(String username, BigDecimal amount);

    void changeTariff(String username, Long tariffId);

    void updateProfile(String username, UpdateProfileRequest request);

    List<ActivePromotionDto> getActivePromotions();

    ClientTariffDetailsDto getClientProfile(String username);

    void changeTariffWithPromotion(String username, Long promotionId);

    ClientDashboardDto getClientDashboard(String username);

    ClientTrafficPageDto getClientTrafficPage(String username, int page, int size);
}
