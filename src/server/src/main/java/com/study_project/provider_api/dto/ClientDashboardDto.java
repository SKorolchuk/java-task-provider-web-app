package com.study_project.provider_api.dto;

import com.study_project.provider_api.model.Tariff;
import java.util.List;

public class ClientDashboardDto {
    private ClientTariffDetailsDto info;
    private List<Tariff> availableTariffs;
    private List<ActivePromotionDto> promotions;

    public ClientDashboardDto() {
    }

    public ClientDashboardDto(ClientTariffDetailsDto info, List<Tariff> availableTariffs,
            List<ActivePromotionDto> promotions) {
        this.info = info;
        this.availableTariffs = availableTariffs;
        this.promotions = promotions;
    }

    public ClientTariffDetailsDto getInfo() {
        return info;
    }

    public void setInfo(ClientTariffDetailsDto info) {
        this.info = info;
    }

    public List<Tariff> getAvailableTariffs() {
        return availableTariffs;
    }

    public void setAvailableTariffs(List<Tariff> availableTariffs) {
        this.availableTariffs = availableTariffs;
    }

    public List<ActivePromotionDto> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<ActivePromotionDto> promotions) {
        this.promotions = promotions;
    }
}
