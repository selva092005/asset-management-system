package com.learn.demo;

import com.learn.demo.util.EmailTemplateBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DemoApplicationTests {

    @Test
    void testBuildGeneralEmail() {
        String title = "System Alert";
        String message = "Your password will expire in 3 days.";
        String appUrl = "http://localhost:5173";

        String html = EmailTemplateBuilder.buildGeneralEmail(title, message, appUrl);

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("AMS Enterprise"));
        assertTrue(html.contains(title));
        assertTrue(html.contains(message));
        assertTrue(html.contains(appUrl));
    }

    @Test
    void testBuildTransferDetailEmail() {
        String title = "Transfer Request PENDING";
        String assetName = "MacBook Pro M3";
        String assetCode = "AMS-EQP-0012";
        String fromLoc = "HQ Office";
        String toLoc = "Remote Office";
        String priority = "HIGH";
        String expectedDate = "2026-06-25";
        String reason = "Employee relocated";
        String requestedBy = "John Doe";
        String status = "PENDING";
        String appUrl = "http://localhost:5173";

        String html = EmailTemplateBuilder.buildTransferDetailEmail(
            title, assetName, assetCode, fromLoc, toLoc, priority, expectedDate, reason, requestedBy, status, appUrl
        );

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains(title));
        assertTrue(html.contains(assetName));
        assertTrue(html.contains(assetCode));
        assertTrue(html.contains(fromLoc));
        assertTrue(html.contains(toLoc));
        assertTrue(html.contains(priority));
        assertTrue(html.contains(expectedDate));
        assertTrue(html.contains(requestedBy));
        assertTrue(html.contains(status));
    }

    @Test
    void testBuildOverdueAlertEmail() {
        String employeeName = "Alice Smith";
        String assetName = "iPhone 15 Pro";
        String assetCode = "AMS-EQP-0044";
        String expectedReturnDate = "2026-06-15";
        long daysOverdue = 4;
        String appUrl = "http://localhost:5173";

        String html = EmailTemplateBuilder.buildOverdueAlertEmail(
            employeeName, assetName, assetCode, expectedReturnDate, daysOverdue, appUrl
        );

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("AMS Warning"));
        assertTrue(html.contains(employeeName));
        assertTrue(html.contains(assetName));
        assertTrue(html.contains(assetCode));
        assertTrue(html.contains(expectedReturnDate));
        assertTrue(html.contains(String.valueOf(daysOverdue)));
    }

    @Test
    void testBuildAssignmentEmail() {
        String title = "Receipt — Asset Allocated";
        String employeeName = "John Doe";
        String employeeEmail = "john@example.com";
        String assetName = "iPad Air";
        String assetCode = "AMS-EQP-0099";
        String actionType = "ALLOCATED";
        String date = "2026-06-19 14:00:00";
        String transactionId = "1781844925031";
        String assignedBy = "IT Admin";
        String appUrl = "http://localhost:5173";
        String backendUrl = "http://localhost:8080";

        String html = EmailTemplateBuilder.buildAssignmentEmail(
            title, employeeName, employeeEmail, assetName, assetCode, actionType, date, transactionId, assignedBy, appUrl, backendUrl
        );

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains(employeeName));
        assertTrue(html.contains(assetName));
        assertTrue(html.contains(assetCode));
        assertTrue(html.contains(actionType));
        assertTrue(html.contains(transactionId));
        assertTrue(html.contains(backendUrl));
    }
}

