package com.learn.demo.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private long totalAssets;
    private long available;
    private long assigned;
    private long damaged;
    private long underMaintenance;
    private long lost;
    private long expiringWarrantyIn30Days;
    private Map<String, Long> countByType;
    private Map<String, Long> countByLocation;
    private Map<String, Long> countByCompany;
}
