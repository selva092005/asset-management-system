package com.learn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferOverviewDTO {
    private long total;
    private long pending;
    private long approved;
    private long rejected;
    private long inTransit;
}
