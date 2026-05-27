package com.learn.demo.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ── FULL REPORT SUMMARY ───────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getFullReport() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Report summary retrieved", reportService.getFullReport())
        );
    }

    // ── EXPORT ALLOCATIONS TO EXCEL ───────────────────────────────────────────
    @GetMapping("/allocations/export")
    public ResponseEntity<byte[]> exportAllocations() {
        try {
            ByteArrayOutputStream out = reportService.exportAllocationsToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("allocations_report.xlsx").build());
            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ── EXPORT TRANSFERS TO EXCEL ─────────────────────────────────────────────
    @GetMapping("/transfers/export")
    public ResponseEntity<byte[]> exportTransfers() {
        try {
            ByteArrayOutputStream out = reportService.exportTransfersToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("transfers_report.xlsx").build());
            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ── EXPORT DISPOSALS TO EXCEL ─────────────────────────────────────────────
    @GetMapping("/disposals/export")
    public ResponseEntity<byte[]> exportDisposals() {
        try {
            ByteArrayOutputStream out = reportService.exportDisposalsToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("disposals_report.xlsx").build());
            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

