package com.learn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocationOverviewDTO {

    private long total;
    private long active;
    private long returned;
    private long overdue;
    private long awaitingReturn;
}