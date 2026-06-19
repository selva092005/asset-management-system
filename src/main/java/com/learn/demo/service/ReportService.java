package com.learn.demo.service;

import java.io.ByteArrayOutputStream;
import com.learn.demo.dto.response.ReportSummaryDTO;

public interface ReportService {
    ReportSummaryDTO getFullReport();
    ByteArrayOutputStream exportAllocationsToExcel();
    ByteArrayOutputStream exportTransfersToExcel();
    ByteArrayOutputStream exportDisposalsToExcel();
    ByteArrayOutputStream exportAuditsToExcel();
}

