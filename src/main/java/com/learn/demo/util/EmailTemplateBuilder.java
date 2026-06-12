package com.learn.demo.util;

public class EmailTemplateBuilder {

    public static String buildGeneralEmail(String title, String message, String appUrl) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "  <meta charset='utf-8'>" +
            "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "  <title>" + title + "</title>" +
            "  <style>" +
            "    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f8fafc; color: #334155; margin: 0; padding: 0; }" +
            "    .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; overflow: hidden; }" +
            "    .header { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); color: #ffffff; padding: 24px; text-align: center; }" +
            "    .header h1 { margin: 0; font-size: 20px; font-weight: 700; letter-spacing: 0.5px; }" +
            "    .content { padding: 32px 24px; line-height: 1.6; }" +
            "    .content h2 { margin-top: 0; color: #0f172a; font-size: 16px; font-weight: 600; }" +
            "    .message-box { background-color: #f1f5f9; border-left: 4px solid #2563eb; padding: 16px; border-radius: 4px; margin: 20px 0; font-size: 14px; color: #1e293b; font-weight: 500; }" +
            "    .btn-container { text-align: center; margin-top: 30px; }" +
            "    .btn { display: inline-block; background-color: #2563eb; color: #ffffff !important; font-weight: 600; text-decoration: none; padding: 12px 24px; border-radius: 6px; font-size: 13px; transition: background-color 0.2s ease; }" +
            "    .btn:hover { background-color: #1d4ed8; }" +
            "    .footer { background-color: #f8fafc; padding: 20px; text-align: center; font-size: 11px; color: #64748b; border-top: 1px solid #e2e8f0; }" +
            "    .footer p { margin: 4px 0; }" +
            "  </style>" +
            "</head>" +
            "<body>" +
            "  <div class='email-container'>" +
            "    <div class='header'>" +
            "      <h1>Asset Management System (AMS)</h1>" +
            "    </div>" +
            "    <div class='content'>" +
            "      <h2>" + title + "</h2>" +
            "      <p>Hello,</p>" +
            "      <div class='message-box'>" +
            "        " + message + "" +
            "      </div>" +
            "      <p>Please log in to your dashboard to review and take action.</p>" +
            "      <div class='btn-container'>" +
            "        <a href='" + appUrl + "' class='btn' target='_blank'>Go to Dashboard</a>" +
            "      </div>" +
            "    </div>" +
            "    <div class='footer'>" +
            "      <p>This is an automated notification. Please do not reply directly to this email.</p>" +
            "      <p>&copy; " + java.time.Year.now().getValue() + " AMS Enterprise. All rights reserved.</p>" +
            "    </div>" +
            "  </div>" +
            "</body>" +
            "</html>";
    }

    public static String buildTransferDetailEmail(
            String title,
            String assetName,
            String assetCode,
            String fromLoc,
            String toLoc,
            String priority,
            String expectedDate,
            String reason,
            String requestedBy,
            String status,
            String appUrl
    ) {
        String priorityColor = "HIGH".equalsIgnoreCase(priority) ? "#ef4444" : "LOW".equalsIgnoreCase(priority) ? "#64748b" : "#f59e0b";
        String statusBg = "PENDING".equalsIgnoreCase(status) ? "#fef3c7" : "IN_TRANSIT".equalsIgnoreCase(status) ? "#e0f2fe" : "APPROVED".equalsIgnoreCase(status) ? "#d1fae5" : "#fee2e2";
        String statusText = "PENDING".equalsIgnoreCase(status) ? "#d97706" : "IN_TRANSIT".equalsIgnoreCase(status) ? "#0284c7" : "APPROVED".equalsIgnoreCase(status) ? "#059669" : "#dc2626";

        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "  <meta charset='utf-8'>" +
            "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "  <title>" + title + "</title>" +
            "  <style>" +
            "    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f8fafc; color: #334155; margin: 0; padding: 0; }" +
            "    .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; overflow: hidden; }" +
            "    .header { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); color: #ffffff; padding: 24px; text-align: center; }" +
            "    .header h1 { margin: 0; font-size: 20px; font-weight: 700; letter-spacing: 0.5px; }" +
            "    .content { padding: 32px 24px; line-height: 1.6; }" +
            "    .content h2 { margin-top: 0; color: #0f172a; font-size: 16px; font-weight: 600; }" +
            "    .transfer-card { border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; background-color: #f8fafc; margin: 20px 0; }" +
            "    .transfer-title { font-weight: 700; color: #1e1b4b; margin-bottom: 8px; font-size: 15px; }" +
            "    .route-container { display: flex; align-items: center; gap: 8px; margin: 15px 0; font-size: 13px; }" +
            "    .from-loc { color: #475569; font-weight: 500; }" +
            "    .arrow { color: #2563eb; font-weight: 800; font-size: 16px; margin: 0 8px; }" +
            "    .to-loc { color: #2563eb; font-weight: 700; }" +
            "    .details-table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 12.5px; }" +
            "    .details-table td { padding: 8px 0; border-bottom: 1px solid #f1f5f9; }" +
            "    .details-table td.label { color: #64748b; font-weight: 600; width: 35%; }" +
            "    .details-table td.value { color: #0f172a; font-weight: 500; }" +
            "    .priority-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700; color: #ffffff; }" +
            "    .status-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700; }" +
            "    .btn-container { text-align: center; margin-top: 30px; }" +
            "    .btn { display: inline-block; background-color: #2563eb; color: #ffffff !important; font-weight: 600; text-decoration: none; padding: 12px 24px; border-radius: 6px; font-size: 13px; transition: background-color 0.2s ease; }" +
            "    .footer { background-color: #f8fafc; padding: 20px; text-align: center; font-size: 11px; color: #64748b; border-top: 1px solid #e2e8f0; }" +
            "  </style>" +
            "</head>" +
            "<body>" +
            "  <div class='email-container'>" +
            "    <div class='header'>" +
            "      <h1>Asset Management System (AMS)</h1>" +
            "    </div>" +
            "    <div class='content'>" +
            "      <h2>" + title + "</h2>" +
            "      <p>Hello,</p>" +
            "      <p>An asset transfer request requires your attention/review:</p>" +
            "      <div class='transfer-card'>" +
            "        <div class='transfer-title'>" + assetName + " (" + assetCode + ")</div>" +
            "        <div class='route-container'>" +
            "          <span class='from-loc'>" + fromLoc + "</span>" +
            "          <span class='arrow'>&rarr;</span>" +
            "          <span class='to-loc'>" + toLoc + "</span>" +
            "        </div>" +
            "        <table class='details-table'>" +
            "          <tr>" +
            "            <td class='label'>Priority</td>" +
            "            <td class='value'><span class='priority-badge' style='background-color: " + priorityColor + ";'>" + priority + "</span></td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Status</td>" +
            "            <td class='value'><span class='status-badge' style='background-color: " + statusBg + "; color: " + statusText + ";'>" + status + "</span></td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Expected Date</td>" +
            "            <td class='value'>" + expectedDate + "</td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Requested By</td>" +
            "            <td class='value'>" + requestedBy + "</td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Reason</td>" +
            "            <td class='value'>" + reason + "</td>" +
            "          </tr>" +
            "        </table>" +
            "      </div>" +
            "      <div class='btn-container'>" +
            "        <a href='" + appUrl + "/transfers' class='btn' target='_blank'>Manage Transfers</a>" +
            "      </div>" +
            "    </div>" +
            "    <div class='footer'>" +
            "      <p>This is an automated notification. Please do not reply directly to this email.</p>" +
            "      <p>&copy; " + java.time.Year.now().getValue() + " AMS Enterprise. All rights reserved.</p>" +
            "    </div>" +
            "  </div>" +
            "</body>" +
            "</html>";
    }

    public static String buildAssignmentEmail(
            String title,
            String employeeName,
            String assetName,
            String assetCode,
            String actionType, // "ALLOCATED" | "RETURNED"
            String date,
            String appUrl
    ) {
        String actionBg = "ALLOCATED".equalsIgnoreCase(actionType) ? "#d1fae5" : "#fee2e2";
        String actionText = "ALLOCATED".equalsIgnoreCase(actionType) ? "#065f46" : "#991b1b";

        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "  <meta charset='utf-8'>" +
            "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "  <title>" + title + "</title>" +
            "  <style>" +
            "    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f8fafc; color: #334155; margin: 0; padding: 0; }" +
            "    .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; overflow: hidden; }" +
            "    .header { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); color: #ffffff; padding: 24px; text-align: center; }" +
            "    .header h1 { margin: 0; font-size: 20px; font-weight: 700; letter-spacing: 0.5px; }" +
            "    .content { padding: 32px 24px; line-height: 1.6; }" +
            "    .content h2 { margin-top: 0; color: #0f172a; font-size: 16px; font-weight: 600; }" +
            "    .receipt-card { border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; background-color: #f8fafc; margin: 20px 0; }" +
            "    .receipt-title { font-weight: 700; color: #1e1b4b; margin-bottom: 15px; font-size: 15px; text-align: center; border-bottom: 2px dashed #e2e8f0; padding-bottom: 10px; }" +
            "    .details-table { width: 100%; border-collapse: collapse; font-size: 12.5px; }" +
            "    .details-table td { padding: 8px 0; border-bottom: 1px solid #f1f5f9; }" +
            "    .details-table td.label { color: #64748b; font-weight: 600; width: 35%; }" +
            "    .details-table td.value { color: #0f172a; font-weight: 500; }" +
            "    .action-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700; }" +
            "    .btn-container { text-align: center; margin-top: 30px; }" +
            "    .btn { display: inline-block; background-color: #2563eb; color: #ffffff !important; font-weight: 600; text-decoration: none; padding: 12px 24px; border-radius: 6px; font-size: 13px; transition: background-color 0.2s ease; }" +
            "    .footer { background-color: #f8fafc; padding: 20px; text-align: center; font-size: 11px; color: #64748b; border-top: 1px solid #e2e8f0; }" +
            "  </style>" +
            "</head>" +
            "<body>" +
            "  <div class='email-container'>" +
            "    <div class='header'>" +
            "      <h1>Asset Management System (AMS)</h1>" +
            "    </div>" +
            "    <div class='content'>" +
            "      <h2>" + title + "</h2>" +
            "      <p>Dear " + employeeName + ",</p>" +
            "      <p>This is a formal receipt of asset activity recorded on your account:</p>" +
            "      <div class='receipt-card'>" +
            "        <div class='receipt-title'>Asset Transaction Receipt</div>" +
            "        <table class='details-table'>" +
            "          <tr>" +
            "            <td class='label'>Asset Name</td>" +
            "            <td class='value'>" + assetName + "</td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Asset Code</td>" +
            "            <td class='value'>" + assetCode + "</td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Activity Type</td>" +
            "            <td class='value'><span class='action-badge' style='background-color: " + actionBg + "; color: " + actionText + ";'>" + actionType + "</span></td>" +
            "          </tr>" +
            "          <tr>" +
            "            <td class='label'>Date Processed</td>" +
            "            <td class='value'>" + date + "</td>" +
            "          </tr>" +
            "        </table>" +
            "      </div>" +
            "      <div class='btn-container'>" +
            "        <a href='" + appUrl + "' class='btn' target='_blank'>View My Allocated Assets</a>" +
            "      </div>" +
            "    </div>" +
            "    <div class='footer'>" +
            "      <p>This is an automated notification. Please do not reply directly to this email.</p>" +
            "      <p>&copy; " + java.time.Year.now().getValue() + " AMS Enterprise. All rights reserved.</p>" +
            "    </div>" +
            "  </div>" +
            "</body>" +
            "</html>";
    }
}
