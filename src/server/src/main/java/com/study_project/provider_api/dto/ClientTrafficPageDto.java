package com.study_project.provider_api.dto;

import java.util.List;
import java.util.Map;

public class ClientTrafficPageDto {
    private List<Map<String, Object>> trafficHistory;
    private List<Map<String, Object>> paymentsHistory;
    private int trafficTotalPages;

    public ClientTrafficPageDto() {
    }

    public ClientTrafficPageDto(List<Map<String, Object>> trafficHistory, List<Map<String, Object>> paymentsHistory,
            int trafficTotalPages) {
        this.trafficHistory = trafficHistory;
        this.paymentsHistory = paymentsHistory;
        this.trafficTotalPages = trafficTotalPages;
    }

    public List<Map<String, Object>> getTrafficHistory() {
        return trafficHistory;
    }

    public void setTrafficHistory(List<Map<String, Object>> trafficHistory) {
        this.trafficHistory = trafficHistory;
    }

    public List<Map<String, Object>> getPaymentsHistory() {
        return paymentsHistory;
    }

    public void setPaymentsHistory(List<Map<String, Object>> paymentsHistory) {
        this.paymentsHistory = paymentsHistory;
    }

    public int getTrafficTotalPages() {
        return trafficTotalPages;
    }

    public void setTrafficTotalPages(int trafficTotalPages) {
        this.trafficTotalPages = trafficTotalPages;
    }
}
