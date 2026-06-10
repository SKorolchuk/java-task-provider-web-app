package com.study_project.provider_api.service;

import com.study_project.provider_api.dto.ClientsPageDto;

import java.math.BigDecimal;

public interface AdminService {
    ClientsPageDto getClientsPage(int page, int size);

    void changeBlockStatus(Long userId, boolean block);

    void chargeClientForTraffic(Long userId, BigDecimal trafficGb, BigDecimal cost);
}
