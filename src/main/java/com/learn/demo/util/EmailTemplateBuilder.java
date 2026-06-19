package com.learn.demo.util;

public class EmailTemplateBuilder {

    private static String backendUrl = "http://localhost:8080";

    public static void setBackendUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            backendUrl = url;
        }
    }

    public static String buildGeneralEmail(String title, String message, String appUrl) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>" + title + "</title>" +
                "  <style>" +
                "    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap');"
                +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }" +
                "    body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; color: #16212E; -webkit-font-smoothing: antialiased; padding: 40px 10px; }"
                +
                "    .wrapper { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 2px dashed #CBD5E1; box-shadow: 0 4px 20px rgba(0,0,0,0.03); overflow: hidden; }"
                +
                "    .stripe-accent { height: 4px; background: linear-gradient(90deg, #0A2F63 0%, #1768C9 100%); }" +
                "    .header-bar { padding: 24px 32px; border-bottom: 1px solid #F1F5F9; }" +
                "    .header-table { width: 100%; border-collapse: collapse; }" +
                "    .logo-container { display: flex; align-items: center; gap: 12px; }" +
                "    .logo-img-wrapper { display: inline-block; width: 36px; height: 36px; border-radius: 50%; overflow: hidden; }"
                +
                "    .company-name { font-family: 'Outfit', sans-serif; font-weight: 800; font-size: 15px; color: #0F172A; line-height: 1.2; letter-spacing: -0.2px; }"
                +
                "    .company-sub { font-size: 10.5px; color: #64748B; font-weight: 500; margin-top: 1px; }" +
                "    .trust-badge { text-align: right; font-size: 11px; color: #64748B; font-weight: 600; font-family: 'Outfit', sans-serif; letter-spacing: 0.2px; }"
                +
                "    .content-area { padding: 32px; }" +
                "    .title-text { font-family: 'Outfit', sans-serif; font-size: 24px; font-weight: 800; color: #0F172A; margin-bottom: 16px; letter-spacing: -0.5px; line-height: 1.3; }"
                +
                "    .message-text { font-size: 14.5px; color: #334155; line-height: 1.6; margin-bottom: 24px; }" +
                "    .btn-container { text-align: center; margin: 36px 0 12px; }" +
                "    .btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; background: linear-gradient(135deg, #0B4FA0 0%, #3B9CF0 100%); color: #ffffff !important; text-decoration: none; font-family: 'Outfit', sans-serif; font-size: 14px; font-weight: 700; padding: 13px 30px; border-radius: 8px; box-shadow: 0 4px 14px rgba(11, 79, 160, 0.25); transition: all 0.2s ease; }"
                +
                "    .footer-bar { background: #F8FAFC; border-top: 1px solid #E2E8F0; padding: 24px 32px; display: flex; justify-content: space-between; }"
                +
                "    .footer-col { display: flex; align-items: center; gap: 12px; font-size: 13px; color: #475569; }" +
                "    .footer-link { color: #0B4FA0; text-decoration: none; font-weight: 600; }" +
                "    .footer-col-left { text-align: left; }" +
                "    .footer-col-right { justify-content: flex-end; text-align: right; }" +
                "    @media (max-width: 480px) {" +
                "      body { padding: 10px 0; }" +
                "      .wrapper { border-radius: 8px; border: none; box-shadow: none; }" +
                "      .header-bar { padding: 16px 20px; }" +
                "      .content-area { padding: 24px 16px; }" +
                "      .title-text { font-size: 20px; }" +
                "      .message-text { font-size: 13.5px; }" +
                "      .btn { width: 100%; text-align: center; }" +
                "      .footer-bar { padding: 20px 16px; flex-direction: column; gap: 12px; text-align: center; }" +
                "      .footer-col { justify-content: center !important; text-align: center !important; }" +
                "      .footer-col-left, .footer-col-right { justify-content: center !important; text-align: center !important; }"
                +
                "      .trust-badge { display: none; }" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='wrapper'>" +
                "" +
                "    <!-- HEADER BAR -->" +
                "    <div class='header-bar'>" +
                "      <table class='header-table'>" +
                "        <tr>" +
                "          <td>" +
                "            <div class='logo-container'>" +
                "              <div class='logo-img-wrapper'>" +
                "                <img src='" + backendUrl
                + "/ams_no_bg.png' alt='AMS Logo' style='display: block; width: 100%; height: 100%; object-fit: cover;' />"
                +
                "              </div>" +
                "              <div>" +
                "                <div class='company-name'>AMS Enterprise</div>" +
                "                <div class='company-sub'>IT Asset Management System</div>" +
                "              </div>" +
                "            </div>" +
                "          </td>" +
                "          <td class='trust-badge'>" +
                "            <span style='display: inline-flex; align-items: center; gap: 6px;'>" +
                "              <svg width='13' height='13' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'></path></svg>"
                +
                "              Secure &bull; Enterprise &bull; Trusted" +
                "            </span>" +
                "          </td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    <!-- CONTENT AREA -->" +
                "    <div class='content-area'>" +
                "      <h1 class='title-text'>" + title + "</h1>" +
                "      <p class='message-text'>Hello,</p>" +
                "      <p class='message-text'>" + message + "</p>" +
                "      <p class='message-text'>Please log in to your dashboard to review and take action.</p>" +
                "      <!-- CTA BUTTON -->" +
                "      <div class='btn-container'>" +
                "        <a href='" + appUrl + "' target='_blank' class='btn'>" +
                "          <svg width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='#ffffff' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M15 3h6v6M10 14L21 3M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6'></path></svg>"
                +
                "          <span>Go to Dashboard</span>" +
                "        </a>" +
                "      </div>" +
                "    </div>" +
                "    <!-- FOOTER BAR -->" +
                "    <div class='footer-bar'>" +
                "      <div class='footer-col footer-col-left'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M3 18v-6a9 9 0 0 1 18 0v6'></path><path d='M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z'></path></svg>"
                +
                "        <div>" +
                "          <div style='font-family: \"Outfit\", sans-serif; font-weight: 700; color: #0F172A;'>Need help?</div>"
                +
                "          <div style='margin-top: 2px;'>Contact <a href='" + appUrl
                + "/support' class='footer-link'>IT Support Team</a></div>" +
                "        </div>" +
                "      </div>" +
                "      <div class='footer-col footer-col-right'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'></path><polyline points='22,6 12,13 2,6'></polyline></svg>"
                +
                "        <div>" +
                "          <div><a href='mailto:support@ams.com' class='footer-link'>support@ams.com</a></div>" +
                "          <div style='color: #64748B; font-weight: 600; margin-top: 2px;'>+91 98765 43210</div>" +
                "        </div>" +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "  <div style='text-align: center; margin-top: 24px; font-family: \"Plus Jakarta Sans\", sans-serif; font-size: 11.5px; color: #64748B; line-height: 1.5;'>"
                +
                "    <p>This is an automated operational notification. Please do not reply directly to this message.</p>"
                +
                "    <p>&copy; " + java.time.Year.now().getValue() + " AMS Enterprise. All rights reserved.</p>" +
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
            String appUrl) {
        String priorityColor = "HIGH".equalsIgnoreCase(priority) ? "#ef4444"
                : "LOW".equalsIgnoreCase(priority) ? "#64748b" : "#f59e0b";
        String statusBg = "PENDING".equalsIgnoreCase(status) ? "#fef3c7"
                : "IN_TRANSIT".equalsIgnoreCase(status) ? "#e0f2fe"
                        : "APPROVED".equalsIgnoreCase(status) ? "#d1fae5" : "#fee2e2";
        String statusText = "PENDING".equalsIgnoreCase(status) ? "#d97706"
                : "IN_TRANSIT".equalsIgnoreCase(status) ? "#0284c7"
                        : "APPROVED".equalsIgnoreCase(status) ? "#059669" : "#dc2626";

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>" + title + "</title>" +
                "  <style>" +
                "    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&family=JetBrains+Mono:wght@500;600;700&display=swap');"
                +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }" +
                "    body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; color: #16212E; -webkit-font-smoothing: antialiased; padding: 40px 10px; }"
                +
                "    .wrapper { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 2px dashed #CBD5E1; box-shadow: 0 4px 20px rgba(0,0,0,0.03); overflow: hidden; }"
                +
                "    .stripe-accent { height: 4px; background: linear-gradient(90deg, #0A2F63 0%, #1768C9 100%); }" +
                "    .header-bar { padding: 24px 32px; border-bottom: 1px solid #F1F5F9; }" +
                "    .header-table { width: 100%; border-collapse: collapse; }" +
                "    .logo-container { display: flex; align-items: center; gap: 12px; }" +
                "    .logo-img-wrapper { display: inline-block; width: 36px; height: 36px; border-radius: 50%; overflow: hidden; }"
                +
                "    .company-name { font-family: 'Outfit', sans-serif; font-weight: 800; font-size: 15px; color: #0F172A; line-height: 1.2; letter-spacing: -0.2px; }"
                +
                "    .company-sub { font-size: 10.5px; color: #64748B; font-weight: 500; margin-top: 1px; }" +
                "    .trust-badge { text-align: right; font-size: 11px; color: #64748B; font-weight: 600; font-family: 'Outfit', sans-serif; letter-spacing: 0.2px; }"
                +
                "    .content-area { padding: 32px; }" +
                "    .title-text { font-family: 'Outfit', sans-serif; font-size: 24px; font-weight: 800; color: #0F172A; margin-bottom: 6px; letter-spacing: -0.5px; line-height: 1.3; }"
                +
                "    .subtitle-text { font-size: 14px; color: #64748B; font-weight: 500; margin-bottom: 28px; }" +
                "    /* Route Graphic */" +
                "    .route-container { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 8px; padding: 16px 20px; margin-bottom: 24px; display: flex; align-items: center; justify-content: center; gap: 14px; }"
                +
                "    .loc-tag { font-family: 'Outfit', sans-serif; font-weight: 700; font-size: 13px; padding: 6px 12px; border-radius: 6px; }"
                +
                "    .loc-from { background: #E2E8F0; color: #475569; }" +
                "    .loc-arrow { color: #0B4FA0; font-weight: 800; font-size: 18px; }" +
                "    .loc-to { background: #EAF3FD; color: #0B4FA0; }" +
                "    /* Ledger Table */" +
                "    .ledger-card { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 10px; padding: 18px 24px; margin-bottom: 28px; }"
                +
                "    .ledger-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #E2E8F0; }"
                +
                "    .ledger-row:last-child { border-bottom: none; }" +
                "    .ledger-left { display: flex; align-items: center; gap: 10px; color: #64748B; font-size: 13.5px; font-weight: 500; }"
                +
                "    .ledger-right { font-weight: 600; font-size: 13.5px; color: #0F172A; text-align: right; }" +
                "    .code-badge { background: #EAF3FD; color: #0B4FA0; border: 1px solid #BDE0FE; padding: 2px 8px; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 12px; font-weight: 600; }"
                +
                "    .priority-pill { display: inline-block; padding: 3px 8px; border-radius: 4px; font-size: 11px; font-weight: 700; color: #ffffff; font-family: 'Outfit', sans-serif; }"
                +
                "    .status-pill { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 700; font-family: 'Outfit', sans-serif; }"
                +
                "    .btn-container { text-align: center; margin: 32px 0 12px; }" +
                "    .btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; background: linear-gradient(135deg, #0B4FA0 0%, #3B9CF0 100%); color: #ffffff !important; text-decoration: none; font-family: 'Outfit', sans-serif; font-size: 14px; font-weight: 700; padding: 13px 30px; border-radius: 8px; box-shadow: 0 4px 14px rgba(11, 79, 160, 0.25); transition: all 0.2s ease; }"
                +
                "    .footer-bar { background: #F8FAFC; border-top: 1px solid #E2E8F0; padding: 24px 32px; display: flex; justify-content: space-between; }"
                +
                "    .footer-col { display: flex; align-items: center; gap: 12px; font-size: 13px; color: #475569; }" +
                "    .footer-link { color: #0B4FA0; text-decoration: none; font-weight: 600; }" +
                "    .footer-col-left { text-align: left; }" +
                "    .footer-col-right { justify-content: flex-end; text-align: right; }" +
                "    @media (max-width: 480px) {" +
                "      body { padding: 10px 0; }" +
                "      .wrapper { border-radius: 8px; border: none; box-shadow: none; }" +
                "      .header-bar { padding: 16px 20px; }" +
                "      .content-area { padding: 24px 16px; }" +
                "      .ledger-card { padding: 12px 16px; }" +
                "      .ledger-row { padding: 10px 0; }" +
                "      .ledger-left { font-size: 12.5px; }" +
                "      .ledger-right { font-size: 12.5px; }" +
                "      .title-text { font-size: 20px; }" +
                "      .subtitle-text { font-size: 13.5px; margin-bottom: 20px; }" +
                "      .btn { width: 100%; text-align: center; }" +
                "      .footer-bar { padding: 20px 16px; flex-direction: column; gap: 12px; text-align: center; }" +
                "      .footer-col { justify-content: center !important; text-align: center !important; }" +
                "      .footer-col-left, .footer-col-right { justify-content: center !important; text-align: center !important; }"
                +
                "      .trust-badge { display: none; }" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='wrapper'>" +
                "" +
                "    <!-- HEADER BAR -->" +
                "    <div class='header-bar'>" +
                "      <table class='header-table'>" +
                "        <tr>" +
                "          <td>" +
                "            <div class='logo-container'>" +
                "              <div class='logo-img-wrapper'>" +
                "                <img src='" + backendUrl
                + "/ams_no_bg.png' alt='AMS Logo' style='display: block; width: 100%; height: 100%; object-fit: cover;' />"
                +
                "              </div>" +
                "              <div>" +
                "                <div class='company-name'>AMS Enterprise</div>" +
                "                <div class='company-sub'>IT Asset Management System</div>" +
                "              </div>" +
                "            </div>" +
                "          </td>" +
                "          <td class='trust-badge'>" +
                "            <span style='display: inline-flex; align-items: center; gap: 6px;'>" +
                "              <svg width='13' height='13' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'></path></svg>"
                +
                "              Secure &bull; Enterprise &bull; Trusted" +
                "            </span>" +
                "          </td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    <!-- CONTENT AREA -->" +
                "    <div class='content-area'>" +
                "      <h1 class='title-text'>" + title + "</h1>" +
                "      <p class='subtitle-text'>An asset transfer request requires your review or action.</p>" +
                "      <!-- ROUTE GRAPHIC -->" +
                "      <div class='route-container'>" +
                "        <span class='loc-tag loc-from'>" + fromLoc + "</span>" +
                "        <span class='loc-arrow'>&rarr;</span>" +
                "        <span class='loc-to loc-tag'>" + toLoc + "</span>" +
                "      </div>" +
                "      <!-- LEDGER DETAILS -->" +
                "      <div class='ledger-card'>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='2' y='3' width='20' height='14' rx='2' ry='2'></rect><line x1='2' y1='20' x2='22' y2='20'></line><line x1='12' y1='17' x2='12' y2='20'></line></svg>"
                +
                "            <span>Asset Name</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + assetName + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z'></path><line x1='7' y1='7' x2='7.01' y2='7'></line></svg>"
                +
                "            <span>Asset Code</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='code-badge'>" + assetCode + "</span></div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'></circle><polyline points='12 6 12 12 16 14'></polyline></svg>"
                +
                "            <span>Expected Date</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + expectedDate + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'></path><circle cx='12' cy='7' r='4'></circle></svg>"
                +
                "            <span>Requested By</span>" +
                "          </div>" +
                "          <div class='ledger-right' style='font-family: \"Outfit\", sans-serif; font-weight: 700;'>"
                + requestedBy + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z'></path></svg>"
                +
                "            <span>Reason</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + reason + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6'></path></svg>"
                +
                "            <span>Priority</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='priority-pill' style='background-color: "
                + priorityColor + ";'>" + priority + "</span></div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'></path></svg>"
                +
                "            <span>Status</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='status-pill' style='background: " + statusBg
                + "; color: " + statusText + ";'>" + status + "</span></div>" +
                "        </div>" +
                "      </div>" +
                "      <!-- CTA BUTTON -->" +
                "      <div class='btn-container'>" +
                "        <a href='" + appUrl + "/transfers' target='_blank' class='btn'>" +
                "          <svg width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='#ffffff' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2'></path><rect x='8' y='2' width='8' height='4' rx='1' ry='1'></rect></svg>"
                +
                "          <span>Manage Transfers</span>" +
                "        </a>" +
                "      </div>" +
                "    </div>" +
                "    <!-- FOOTER BAR -->" +
                "    <div class='footer-bar'>" +
                "      <div class='footer-col footer-col-left'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M3 18v-6a9 9 0 0 1 18 0v6'></path><path d='M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z'></path></svg>"
                +
                "        <div>" +
                "          <div style='font-family: \"Outfit\", sans-serif; font-weight: 700; color: #0F172A;'>Need help?</div>"
                +
                "          <div style='margin-top: 2px;'>Contact <a href='" + appUrl
                + "/support' class='footer-link'>IT Support Team</a></div>" +
                "        </div>" +
                "      </div>" +
                "      <div class='footer-col footer-col-right'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'></path><polyline points='22,6 12,13 2,6'></polyline></svg>"
                +
                "        <div>" +
                "          <div><a href='mailto:support@ams.com' class='footer-link'>support@ams.com</a></div>" +
                "          <div style='color: #64748B; font-weight: 600; margin-top: 2px;'>+91 98765 43210</div>" +
                "        </div>" +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "  <div style='text-align: center; margin-top: 24px; font-family: \"Plus Jakarta Sans\", sans-serif; font-size: 11.5px; color: #64748B; line-height: 1.5;'>"
                +
                "    <p>This is an automated operational notification. Please do not reply directly to this message.</p>"
                +
                "    <p>&copy; " + java.time.Year.now().getValue() + " AMS Enterprise. All rights reserved.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    public static String buildAssignmentEmail(
            String title,
            String employeeName,
            String employeeEmail,
            String assetName,
            String assetCode,
            String actionType, // "ALLOCATED" | "RETURNED"
            String date,
            String transactionId,
            String assignedBy,
            String appUrl,
            String backendUrl) {
        boolean isAllocated = "ALLOCATED".equalsIgnoreCase(actionType);
        String actionAdmin = (assignedBy != null && !assignedBy.trim().isEmpty()) ? assignedBy : "IT Admin";
        String statusPillBg = isAllocated ? "linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)"
                : "linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)";
        String statusPillColor = isAllocated ? "#065f46" : "#0369a1";
        String statusPillText = isAllocated ? "ALLOCATED" : "RETURNED";

        String passNo = String.format("%05d", Math.abs(assetCode != null ? assetCode.hashCode() % 100000 : 99999));

        String dateOnly = date;
        String timeOnly = "N/A";
        if (date != null && date.contains(" ")) {
            String[] parts = date.split(" ");
            dateOnly = parts[0];
            timeOnly = parts.length > 1 ? parts[1] : "N/A";
        }

        // Format dates beautifully
        String formattedDateTime = formatDateTime(date);

        // Generate Transaction ID format
        String transPrefix = isAllocated ? "AMS-ALC-" : "AMS-RET-";
        String dateCompact = dateOnly != null ? dateOnly.replace("-", "").replace("/", "") : "TODAY";
        String transIdFormatted = transPrefix + dateCompact + "-" + passNo;

        String stripeGradient = isAllocated
                ? "linear-gradient(90deg, #059669 0%, #10B981 100%)"
                : "linear-gradient(90deg, #0A2F63 0%, #1768C9 100%)";
        String primaryBrandColor = isAllocated ? "#059669" : "#0B4FA0";

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>" + title + "</title>" +
                "  <style>" +
                "    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&family=JetBrains+Mono:wght@500;600;700&display=swap');"
                +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }" +
                "    body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; color: #16212E; -webkit-font-smoothing: antialiased; padding: 40px 10px; }"
                +
                "    .wrapper { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 2px dashed #CBD5E1; box-shadow: 0 4px 20px rgba(0,0,0,0.03); overflow: hidden; }"
                +
                "    .stripe-accent { height: 4px; background: " + stripeGradient + "; }" +
                "    .header-bar { padding: 24px 32px; border-bottom: 1px solid #F1F5F9; }" +
                "    .header-table { width: 100%; border-collapse: collapse; }" +
                "    .logo-container { display: flex; align-items: center; gap: 12px; }" +
                "    .logo-img-wrapper { display: inline-block; width: 36px; height: 36px; border-radius: 50%; overflow: hidden; }"
                +
                "    .company-name { font-family: 'Outfit', sans-serif; font-weight: 800; font-size: 15px; color: #0F172A; line-height: 1.2; letter-spacing: -0.2px; }"
                +
                "    .company-sub { font-size: 10.5px; color: #64748B; font-weight: 500; margin-top: 1px; }" +
                "    .trust-badge { text-align: right; font-size: 11px; color: #64748B; font-weight: 600; font-family: 'Outfit', sans-serif; letter-spacing: 0.2px; }"
                +
                "    .content-area { padding: 32px; }" +
                "    @keyframes draw-circle {" +
                "      0% { stroke-dashoffset: 157; }" +
                "      100% { stroke-dashoffset: 0; }" +
                "    }" +
                "    @keyframes draw-check {" +
                "      0% { stroke-dashoffset: 48; }" +
                "      100% { stroke-dashoffset: 0; }" +
                "    }" +
                "    @keyframes check-pop {" +
                "      0% { transform: scale(0.9); opacity: 0; }" +
                "      50% { transform: scale(1.05); }" +
                "      100% { transform: scale(1); opacity: 1; }" +
                "    }" +
                "    .checkmark-wrapper {" +
                "      display: inline-block;" +
                "      width: 64px;" +
                "      height: 64px;" +
                "      margin-bottom: 20px;" +
                "      animation: check-pop 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;" +
                "    }" +
                "    .checkmark-circle-path {" +
                "      stroke-dasharray: 157;" +
                "      stroke-dashoffset: 157;" +
                "      animation: draw-circle 0.6s cubic-bezier(0.22, 1, 0.36, 1) forwards;" +
                "    }" +
                "    .checkmark-check-path {" +
                "      stroke-dasharray: 48;" +
                "      stroke-dashoffset: 48;" +
                "      animation: draw-check 0.4s cubic-bezier(0.22, 1, 0.36, 1) 0.4s forwards;" +
                "    }" +
                "    .title-text { font-family: 'Outfit', sans-serif; font-size: 24px; font-weight: 800; color: #0F172A; margin-bottom: 6px; letter-spacing: -0.5px; line-height: 1.3; }"
                +
                "    .subtitle-text { font-size: 14.5px; color: #64748B; font-weight: 500; margin-bottom: 28px; }" +
                "    .ledger-card { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 10px; padding: 18px 24px; margin-bottom: 24px; }"
                +
                "    .ledger-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #E2E8F0; }"
                +
                "    .ledger-row:last-child { border-bottom: none; }" +
                "    .ledger-left { display: flex; align-items: center; gap: 10px; color: #64748B; font-size: 13.5px; font-weight: 500; }"
                +
                "    .ledger-right { font-weight: 600; font-size: 13.5px; color: #0F172A; text-align: right; }" +
                "    .code-badge { background: #EAF3FD; color: " + primaryBrandColor
                + "; border: 1px solid #BDE0FE; padding: 2px 8px; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 12px; font-weight: 600; }"
                +
                "    .status-pill { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 700; font-family: 'Outfit', sans-serif; }"
                +
                "    .note-box { background: #F8FAFC; border-left: 3px solid " + primaryBrandColor
                + "; border-radius: 4px; padding: 12px 16px; display: flex; align-items: flex-start; gap: 10px; margin-bottom: 24px; font-size: 13px; color: #334155; }"
                +
                "    .attachment-card { border: 1px solid #E2E8F0; border-radius: 8px; padding: 14px 18px; display: flex; align-items: center; justify-content: space-between; margin-bottom: 28px; background: #ffffff; }"
                +
                "    .btn-container { text-align: center; margin: 32px 0 12px; }" +
                "    .btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; background: linear-gradient(135deg, #0B4FA0 0%, #3B9CF0 100%); color: #ffffff !important; text-decoration: none; font-family: 'Outfit', sans-serif; font-size: 14px; font-weight: 700; padding: 13px 30px; border-radius: 8px; box-shadow: 0 4px 14px rgba(11, 79, 160, 0.25); transition: all 0.2s ease; }"
                +
                "    .footer-bar { background: #F8FAFC; border-top: 1px solid #E2E8F0; padding: 24px 32px; display: flex; justify-content: space-between; }"
                +
                "    .footer-col { display: flex; align-items: center; gap: 12px; font-size: 13px; color: #475569; }" +
                "    .footer-link { color: #0B4FA0; text-decoration: none; font-weight: 600; }" +
                "    @keyframes spark-1 { 0% { transform: translate(0, 0) scale(1); opacity: 1; } 100% { transform: translate(-20px, -20px) scale(0); opacity: 0; } }"
                +
                "    @keyframes spark-2 { 0% { transform: translate(0, 0) scale(1); opacity: 1; } 100% { transform: translate(20px, -20px) scale(0); opacity: 0; } }"
                +
                "    @keyframes spark-3 { 0% { transform: translate(0, 0) scale(1); opacity: 1; } 100% { transform: translate(25px, 0) scale(0); opacity: 0; } }"
                +
                "    @keyframes spark-4 { 0% { transform: translate(0, 0) scale(1); opacity: 1; } 100% { transform: translate(20px, 20px) scale(0); opacity: 0; } }"
                +
                "    @keyframes spark-5 { 0% { transform: translate(0, 0) scale(1); opacity: 1; } 100% { transform: translate(-20px, 20px) scale(0); opacity: 0; } }"
                +
                "    @keyframes spark-6 { 0% { transform: translate(0, 0) scale(1); opacity: 1; } 100% { transform: translate(-25px, 0) scale(0); opacity: 0; } }"
                +
                "    .spark { opacity: 0; transform-origin: center; }" +
                "    .s1 { animation: spark-1 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.6s forwards; }" +
                "    .s2 { animation: spark-2 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.6s forwards; }" +
                "    .s3 { animation: spark-3 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.6s forwards; }" +
                "    .s4 { animation: spark-4 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.6s forwards; }" +
                "    .s5 { animation: spark-5 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.6s forwards; }" +
                "    .s6 { animation: spark-6 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.6s forwards; }" +
                "    @media (max-width: 480px) {" +
                "      body { padding: 10px 0; }" +
                "      .wrapper { border-radius: 8px; border: none; box-shadow: none; }" +
                "      .header-bar { padding: 16px 20px; }" +
                "      .content-area { padding: 24px 16px; }" +
                "      .ledger-card { padding: 12px 16px; }" +
                "      .ledger-row { padding: 10px 0; }" +
                "      .ledger-left { font-size: 12.5px; }" +
                "      .ledger-right { font-size: 12.5px; }" +
                "      .title-text { font-size: 20px; }" +
                "      .subtitle-text { font-size: 13.5px; margin-bottom: 20px; }" +
                "      .btn { width: 100%; text-align: center; }" +
                "      .footer-bar { padding: 20px 16px; flex-direction: column; gap: 12px; text-align: center; }" +
                "      .footer-col { justify-content: center !important; text-align: center !important; }" +
                "      .footer-col-left, .footer-col-right { justify-content: center !important; text-align: center !important; }"
                +
                "      .trust-badge { display: none; }" +
                "      .attachment-card { flex-direction: column !important; align-items: stretch !important; gap: 14px !important; }"
                +
                "      .attachment-info { width: 100% !important; }" +
                "      .attachment-download { display: flex !important; width: 100% !important; box-sizing: border-box !important; justify-content: center !important; padding: 10px !important; background: #F8FAFC !important; border: 1px solid #E2E8F0 !important; border-radius: 6px !important; }"
                +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='wrapper'>" +
                "" +
                "    <!-- HEADER BAR -->" +
                "    <div class='header-bar'>" +
                "      <table class='header-table'>" +
                "        <tr>" +
                "          <td>" +
                "            <div class='logo-container'>" +
                "              <div class='logo-img-wrapper'>" +
                "                <img src='" + backendUrl
                + "/ams_no_bg.png' alt='AMS Logo' style='display: block; width: 100%; height: 100%; object-fit: cover;' />"
                +
                "              </div>" +
                "              <div>" +
                "                <div class='company-name'>AMS Enterprise</div>" +
                "                <div class='company-sub'>IT Asset Management System</div>" +
                "              </div>" +
                "            </div>" +
                "          </td>" +
                "          <td class='trust-badge'>" +
                "            <span style='display: inline-flex; align-items: center; gap: 6px;'>" +
                "              <svg width='13' height='13' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'></path></svg>"
                +
                "              Secure &bull; Enterprise &bull; Trusted" +
                "            </span>" +
                "          </td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    <!-- CONTENT AREA -->" +
                "    <div class='content-area' style='text-align: center;'>" +
                "      <div class='checkmark-wrapper'>" +
                "        <svg width='64' height='64' viewBox='0 0 52 52' fill='none' xmlns='http://www.w3.org/2000/svg' style='display: block; margin: 0 auto; overflow: visible;'>"
                +
                "          <circle cx='26' cy='26' r='25' fill='" + (isAllocated ? "#ECFDF5" : "#F0F9FF") + "' />" +
                "          <circle class='checkmark-circle-path' cx='26' cy='26' r='25' stroke='"
                + (isAllocated ? "#10B981" : "#0284C7") + "' stroke-width='3' stroke-linecap='round' />" +
                "          <path class='checkmark-check-path' d='M16 26l7 7 14-14' stroke='"
                + (isAllocated ? "#059669" : "#0369A1")
                + "' stroke-width='4' stroke-linecap='round' stroke-linejoin='round' />" +
                "          <circle class='spark s1' cx='26' cy='26' r='2.5' fill='"
                + (isAllocated ? "#10B981" : "#0284C7") + "' />" +
                "          <circle class='spark s2' cx='26' cy='26' r='2.5' fill='"
                + (isAllocated ? "#34D399" : "#38BDF8") + "' />" +
                "          <circle class='spark s3' cx='26' cy='26' r='2.5' fill='"
                + (isAllocated ? "#059669" : "#0369A1") + "' />" +
                "          <circle class='spark s4' cx='26' cy='26' r='2.5' fill='"
                + (isAllocated ? "#10B981" : "#0284C7") + "' />" +
                "          <circle class='spark s5' cx='26' cy='26' r='2.5' fill='"
                + (isAllocated ? "#34D399" : "#38BDF8") + "' />" +
                "          <circle class='spark s6' cx='26' cy='26' r='2.5' fill='"
                + (isAllocated ? "#059669" : "#0369A1") + "' />" +
                "        </svg>" +
                "      </div>" +
                "      <h1 class='title-text'>Asset " + (isAllocated ? "Allocated" : "Returned") + " Successfully</h1>"
                +
                "      <p class='subtitle-text'>"
                + (isAllocated ? "The following asset has been officially assigned to you."
                        : "The following asset return has been recorded.")
                + "</p>" +
                "      <!-- LEDGER DETAILS -->" +
                "      <div class='ledger-card' style='text-align: left;'>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'></circle><polyline points='12 6 12 12 16 14'></polyline></svg>"
                +
                "            <span>Transaction ID</span>" +
                "          </div>" +
                "          <div class='ledger-right' style='font-family: \"JetBrains Mono\", monospace; font-weight: 700; color: #0F172A;'>"
                + transIdFormatted + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='4' width='18' height='18' rx='2' ry='2'></rect><line x1='16' y1='2' x2='16' y2='6'></line><line x1='8' y1='2' x2='8' y2='6'></line><line x1='3' y1='10' x2='21' y2='10'></line></svg>"
                +
                "            <span>Date &amp; Time</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + formattedDateTime + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='2' y='3' width='20' height='14' rx='2' ry='2'></rect><line x1='2' y1='20' x2='22' y2='20'></line><line x1='12' y1='17' x2='12' y2='20'></line></svg>"
                +
                "            <span>Asset Name</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + assetName + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z'></path><line x1='7' y1='7' x2='7.01' y2='7'></line></svg>"
                +
                "            <span>Asset Code</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='code-badge'>" + assetCode + "</span></div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'></path><circle cx='12' cy='7' r='4'></circle></svg>"
                +
                "            <span>Assigned To</span>" +
                "          </div>" +
                "          <div class='ledger-right' style='font-family: \"Outfit\", sans-serif; font-weight: 700;'>"
                + employeeName + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='2' y='7' width='20' height='14' rx='2' ry='2'></rect><path d='M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16'></path></svg>"
                +
                "            <span>Assigned By</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + actionAdmin + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'></path></svg>"
                +
                "            <span>Status</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='status-pill' style='background: " + statusPillBg
                + "; color: " + statusPillColor + ";'>" + statusPillText + "</span></div>" +
                "        </div>" +
                "      </div>" +
                "      <!-- NOTE BOX -->" +
                "      <div class='note-box' style='text-align: left;'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='" + primaryBrandColor
                + "' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round' style='flex-shrink: 0; margin-top: 1px;'><circle cx='12' cy='12' r='10'></circle><line x1='12' y1='16' x2='12' y2='12'></line><line x1='12' y1='8' x2='12.01' y2='8'></line></svg>"
                +
                "        <span><strong>Note:</strong> Please retain this receipt for your records and compliance purposes.</span>"
                +
                "      </div>" +
                "      <!-- DOTTED CUT-OFF SLIP -->" +
                "      <table border='0' cellpadding='0' cellspacing='0' style='width: 100%; margin: 32px 0;'>" +
                "        <tr>" +
                "          <td style='border-top: 2px dashed #E2E8F0; height: 1px; line-height: 1px; font-size: 1px;'>&nbsp;</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style='text-align: center; padding-top: 8px;'>" +
                "            <span style='font-family: \"Outfit\", sans-serif; font-size: 11px; color: #64748B; font-weight: 700; letter-spacing: 1px; text-transform: uppercase;'>&#9986; TEAR ALONG DOTTED LINE FOR IT COMPLIANCE SLIP</span>" +
                "          </td>" +
                "        </tr>" +
                "      </table>" +
                "      <!-- ATTACHMENT CARD -->" +
                "      <div class='attachment-card'>" +
                "        <div class='attachment-info' style='display: flex; align-items: center; gap: 14px; text-align: left;'>"
                +
                "          <div style='position: relative; width: 34px; height: 38px; background: #fee2e2; border-radius: 4px; border: 1px solid #fecaca; display: flex; align-items: center; justify-content: center;'>"
                +
                "            <div style='position: absolute; top: 0; right: 0; width: 8px; height: 8px; background: #ffffff; border-bottom: 1px solid #fecaca; border-left: 1px solid #fecaca; border-radius: 0 4px 0 0;'></div>"
                +
                "            <span style='font-family: \"Outfit\", sans-serif; font-size: 9px; font-weight: 800; color: #b91c1c; margin-top: 8px;'>PDF</span>"
                +
                "          </div>" +
                "          <div>" +
                "            <div style='font-size: 13px; font-weight: 600; color: #0f172a;'>Asset_Allocation_Receipt.pdf</div>"
                +
                "            <div style='font-size: 11.5px; color: #64748B; margin-top: 2px;'>PDF Document &bull; 128 KB</div>"
                +
                "          </div>" +
                "        </div>" +
                "        <a class='attachment-download' href='" + backendUrl + "/api/files/Receipt_" + actionType + "_"
                + transactionId
                + ".pdf' target='_blank' download style='display: flex; align-items: center; gap: 6px; font-family: \"Outfit\", sans-serif; font-size: 13px; font-weight: 700; color: #0B4FA0; text-decoration: none;'>"
                +
                "          <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4'></path><polyline points='7 10 12 15 17 10'></polyline><line x1='12' y1='15' x2='12' y2='3'></line></svg>"
                +
                "          <span>Download</span>" +
                "        </a>" +
                "      </div>" +
                "      <!-- CTA BUTTON -->" +
                "      <div class='btn-container'>" +
                "        <a href='" + appUrl + "' target='_blank' class='btn'>" +
                "          <svg width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='#ffffff' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z'></path><polyline points='14 2 14 8 20 8'></polyline><line x1='16' y1='13' x2='8' y2='13'></line><line x1='16' y1='17' x2='8' y2='17'></line><polyline points='10 9 9 9 8 9'></polyline></svg>"
                +
                "          <span>View My Assets</span>" +
                "        </a>" +
                "      </div>" +
                "    </div>" +
                "    <!-- FOOTER BAR -->" +
                "    <div class='footer-bar'>" +
                "      <div class='footer-col footer-col-left'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M3 18v-6a9 9 0 0 1 18 0v6'></path><path d='M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z'></path></svg>"
                +
                "        <div>" +
                "          <div style='font-family: \"Outfit\", sans-serif; font-weight: 700; color: #0F172A;'>Need help?</div>"
                +
                "          <div style='margin-top: 2px;'>Contact <a href='" + appUrl
                + "/support' class='footer-link'>IT Support Team</a></div>" +
                "        </div>" +
                "      </div>" +
                "      <div class='footer-col footer-col-right'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#0B4FA0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'></path><polyline points='22,6 12,13 2,6'></polyline></svg>"
                +
                "        <div>" +
                "          <div><a href='mailto:support@ams.com' class='footer-link'>support@ams.com</a></div>" +
                "          <div style='color: #64748B; font-weight: 600; margin-top: 2px;'>+91 98765 43210</div>" +
                "        </div>" +
                "      </div>" +
                "    </div>" +
                "  </div>";
    }

    public static String buildOverdueAlertEmail(
            String employeeName,
            String assetName,
            String assetCode,
            String expectedReturnDate,
            long daysOverdue,
            String appUrl) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>AMS Warning — Asset Return Overdue</title>" +
                "  <style>" +
                "    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&family=JetBrains+Mono:wght@500;600;700&display=swap');"
                +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }" +
                "    body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; color: #16212E; -webkit-font-smoothing: antialiased; padding: 40px 10px; }"
                +
                "    .wrapper { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 2px dashed #CBD5E1; box-shadow: 0 4px 20px rgba(0,0,0,0.03); overflow: hidden; }"
                +
                "    .stripe-accent { height: 4px; background: linear-gradient(90deg, #DC2626 0%, #EF4444 100%); }" +
                "    .header-bar { padding: 24px 32px; border-bottom: 1px solid #F1F5F9; }" +
                "    .header-table { width: 100%; border-collapse: collapse; }" +
                "    .logo-container { display: flex; align-items: center; gap: 12px; }" +
                "    .logo-img-wrapper { display: inline-block; width: 36px; height: 36px; border-radius: 50%; overflow: hidden; }"
                +
                "    .company-name { font-family: 'Outfit', sans-serif; font-weight: 800; font-size: 15px; color: #0F172A; line-height: 1.2; letter-spacing: -0.2px; }"
                +
                "    .company-sub { font-size: 10.5px; color: #64748B; font-weight: 500; margin-top: 1px; }" +
                "    .trust-badge { text-align: right; font-size: 11px; color: #DC2626; font-weight: 600; font-family: 'Outfit', sans-serif; letter-spacing: 0.2px; }"
                +
                "    .content-area { padding: 32px; }" +
                "    .warning-icon { width: 52px; height: 52px; border-radius: 50%; background-color: #FEE2E2; display: inline-flex; align-items: center; justify-content: center; box-shadow: 0 4px 14px rgba(220, 38, 38, 0.15); margin-bottom: 20px; }"
                +
                "    .title-text { font-family: 'Outfit', sans-serif; font-size: 24px; font-weight: 800; color: #B91C1C; margin-bottom: 6px; letter-spacing: -0.5px; line-height: 1.3; }"
                +
                "    .subtitle-text { font-size: 14.5px; color: #64748B; font-weight: 500; margin-bottom: 28px; }" +
                "    .ledger-card { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 10px; padding: 18px 24px; margin-bottom: 24px; }"
                +
                "    .ledger-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #E2E8F0; }"
                +
                "    .ledger-row:last-child { border-bottom: none; }" +
                "    .ledger-left { display: flex; align-items: center; gap: 10px; color: #64748B; font-size: 13.5px; font-weight: 500; }"
                +
                "    .ledger-right { font-weight: 600; font-size: 13.5px; color: #0F172A; text-align: right; }" +
                "    .code-badge { background: #FEE2E2; color: #B91C1C; border: 1px solid #FCA5A5; padding: 2px 8px; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 12px; font-weight: 600; }"
                +
                "    .overdue-pill { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 700; background-color: #FEE2E2; color: #B91C1C; font-family: 'Outfit', sans-serif; }"
                +
                "    .note-box { background: #FFF5F5; border-left: 3px solid #DC2626; border-radius: 4px; padding: 12px 16px; display: flex; align-items: flex-start; gap: 10px; margin-bottom: 24px; font-size: 13px; color: #B91C1C; }"
                +
                "    .btn-container { text-align: center; margin: 32px 0 12px; }" +
                "    .btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; background: linear-gradient(135deg, #DC2626 0%, #EF4444 100%); color: #ffffff !important; text-decoration: none; font-family: 'Outfit', sans-serif; font-size: 14px; font-weight: 700; padding: 13px 30px; border-radius: 8px; box-shadow: 0 4px 14px rgba(220, 38, 38, 0.25); transition: all 0.2s ease; }"
                +
                "    .footer-bar { background: #F8FAFC; border-top: 1px solid #E2E8F0; padding: 24px 32px; display: flex; justify-content: space-between; }"
                +
                "    .footer-col { display: flex; align-items: center; gap: 12px; font-size: 13px; color: #475569; }" +
                "    .footer-link { color: #DC2626; text-decoration: none; font-weight: 600; }" +
                "    .footer-col-left { text-align: left; }" +
                "    .footer-col-right { justify-content: flex-end; text-align: right; }" +
                "    @media (max-width: 480px) {" +
                "      body { padding: 10px 0; }" +
                "      .wrapper { border-radius: 8px; border: none; box-shadow: none; }" +
                "      .header-bar { padding: 16px 20px; }" +
                "      .content-area { padding: 24px 16px; }" +
                "      .ledger-card { padding: 12px 16px; }" +
                "      .ledger-row { padding: 10px 0; }" +
                "      .ledger-left { font-size: 12.5px; }" +
                "      .ledger-right { font-size: 12.5px; }" +
                "      .title-text { font-size: 20px; }" +
                "      .subtitle-text { font-size: 13.5px; margin-bottom: 20px; }" +
                "      .btn { width: 100%; text-align: center; }" +
                "      .footer-bar { padding: 20px 16px; flex-direction: column; gap: 12px; text-align: center; }" +
                "      .footer-col { justify-content: center !important; text-align: center !important; }" +
                "      .footer-col-left, .footer-col-right { justify-content: center !important; text-align: center !important; }"
                +
                "      .trust-badge { display: none; }" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='wrapper'>" +
                "" +
                "    <!-- HEADER BAR -->" +
                "    <div class='header-bar'>" +
                "      <table class='header-table'>" +
                "        <tr>" +
                "          <td>" +
                "            <div class='logo-container'>" +
                "              <div class='logo-img-wrapper'>" +
                "                <img src='" + backendUrl
                + "/ams_no_bg.png' alt='AMS Logo' style='display: block; width: 100%; height: 100%; object-fit: cover;' />"
                +
                "              </div>" +
                "              <div>" +
                "                <div class='company-name'>AMS Enterprise</div>" +
                "                <div class='company-sub'>IT Asset Management System</div>" +
                "              </div>" +
                "            </div>" +
                "          </td>" +
                "          <td class='trust-badge'>" +
                "            <span style='display: inline-flex; align-items: center; gap: 6px;'>" +
                "              <svg width='13' height='13' viewBox='0 0 24 24' fill='none' stroke='#DC2626' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'></path></svg>"
                +
                "              Urgent &bull; Compliance &bull; Action Required" +
                "            </span>" +
                "          </td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    <!-- CONTENT AREA -->" +
                "    <div class='content-area' style='text-align: center;'>" +
                "      <div class='warning-icon'>" +
                "        <svg width='22' height='22' viewBox='0 0 24 24' fill='none' stroke='#DC2626' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z'></path><line x1='12' y1='9' x2='12' y2='13'></line><line x1='12' y1='17' x2='12.01' y2='17'></line></svg>"
                +
                "      </div>" +
                "      <h1 class='title-text'>Overdue Return Notice</h1>" +
                "      <p class='subtitle-text'>Hello " + employeeName
                + ", the following corporate asset is past its return deadline.</p>" +
                "      <!-- LEDGER DETAILS -->" +
                "      <div class='ledger-card' style='text-align: left;'>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='2' y='3' width='20' height='14' rx='2' ry='2'></rect><line x1='2' y1='20' x2='22' y2='20'></line><line x1='12' y1='17' x2='12' y2='20'></line></svg>"
                +
                "            <span>Asset Name</span>" +
                "          </div>" +
                "          <div class='ledger-right'>" + assetName + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z'></path><line x1='7' y1='7' x2='7.01' y2='7'></line></svg>"
                +
                "            <span>Asset Code</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='code-badge'>" + assetCode + "</span></div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'></circle><polyline points='12 6 12 12 16 14'></polyline></svg>"
                +
                "            <span>Return Deadline</span>" +
                "          </div>" +
                "          <div class='ledger-right' style='color: #B91C1C; font-weight: 700;'>" + expectedReturnDate
                + "</div>" +
                "        </div>" +
                "        <div class='ledger-row'>" +
                "          <div class='ledger-left'>" +
                "            <svg width='15' height='15' viewBox='0 0 24 24' fill='none' stroke='#64748B' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'></circle><line x1='12' y1='8' x2='12' y2='12'></line><line x1='12' y1='16' x2='12.01' y2='16'></line></svg>"
                +
                "            <span>Days Overdue</span>" +
                "          </div>" +
                "          <div class='ledger-right'><span class='overdue-pill'>" + daysOverdue
                + " Days Late</span></div>" +
                "        </div>" +
                "      </div>" +
                "      <!-- WARNING NOTE BOX -->" +
                "      <div class='note-box' style='text-align: left;'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#DC2626' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round' style='flex-shrink: 0; margin-top: 1px;'><circle cx='12' cy='12' r='10'></circle><line x1='12' y1='8' x2='12' y2='12'></line><line x1='12' y1='16' x2='12.01' y2='16'></line></svg>"
                +
                "        <span><strong>Important:</strong> Please return this asset to the IT department immediately to avoid account compliance flags.</span>"
                +
                "      </div>" +
                "      <!-- CTA BUTTON -->" +
                "      <div class='btn-container'>" +
                "        <a href='" + appUrl + "' target='_blank' class='btn'>" +
                "          <svg width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='#ffffff' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'><path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'></path><circle cx='12' cy='12' r='3'></circle></svg>"
                +
                "          <span>View My Assets</span>" +
                "        </a>" +
                "      </div>" +
                "    </div>" +
                "    <!-- FOOTER BAR -->" +
                "    <div class='footer-bar'>" +
                "      <div class='footer-col footer-col-left'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#DC2626' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M3 18v-6a9 9 0 0 1 18 0v6'></path><path d='M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z'></path></svg>"
                +
                "        <div>" +
                "          <div style='font-family: \"Outfit\", sans-serif; font-weight: 700; color: #0F172A;'>Need help?</div>"
                +
                "          <div style='margin-top: 2px;'>Contact <a href='" + appUrl
                + "/support' class='footer-link'>IT Support Team</a></div>" +
                "        </div>" +
                "      </div>" +
                "      <div class='footer-col footer-col-right'>" +
                "        <svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#DC2626' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'></path><polyline points='22,6 12,13 2,6'></polyline></svg>"
                +
                "        <div>" +
                "          <div><a href='mailto:support@ams.com' class='footer-link'>support@ams.com</a></div>" +
                "          <div style='color: #64748B; font-weight: 600; margin-top: 2px;'>+91 98765 43210</div>" +
                "        </div>" +
                "      </div>" +
                "    </div>" +
                "  </div>";
    }

    public static String buildReceiptInvoiceHtml(
            String employeeName,
            String employeeEmail,
            String assetName,
            String assetCode,
            String actionType,
            String date,
            String transactionId) {
        String actionTitle = "ALLOCATED".equalsIgnoreCase(actionType) ? "Asset Allocation Handover"
                : "Asset Return Receipt";
        String actionStatus = "ALLOCATED".equalsIgnoreCase(actionType) ? "ACTIVE" : "COMPLETED / RETURNED";
        String statusColor = "ALLOCATED".equalsIgnoreCase(actionType) ? "#0284c7" : "#059669";
        String statusBg = "ALLOCATED".equalsIgnoreCase(actionType) ? "#e0f2fe" : "#d1fae5";

        return "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<head>" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                "  <title>" + actionTitle + "</title>" +
                "  <style type=\"text/css\">" +
                "    body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #1e293b; background-color: #ffffff; margin: 0; padding: 0; }"
                +
                "    .receipt-container { max-width: 600px; margin: 0 auto; padding: 40px 20px; }" +
                "    .header-table { width: 100%; border-collapse: collapse; margin-bottom: 40px; }" +
                "    .logo-title { font-size: 20px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; color: #0f172a; }"
                +
                "    .invoice-label { font-size: 24px; font-weight: 700; text-align: right; color: #64748b; text-transform: uppercase; }"
                +
                "    .divider { height: 1px; background-color: #cbd5e1; margin: 20px 0; }" +
                "    .meta-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; font-size: 13px; }" +
                "    .meta-col { width: 50%; vertical-align: top; }" +
                "    .meta-title { font-weight: 700; color: #475569; margin-bottom: 6px; text-transform: uppercase; font-size: 11px; letter-spacing: 0.5px; }"
                +
                "    .detail-card { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; margin-bottom: 40px; }"
                +
                "    .detail-table { width: 100%; border-collapse: collapse; font-size: 14px; }" +
                "    .detail-table td { padding: 12px 0; border-bottom: 1px solid #e2e8f0; }" +
                "    .detail-table tr:last-child td { border-bottom: none; }" +
                "    .label { color: #64748b; font-weight: 500; }" +
                "    .value { color: #0f172a; font-weight: 600; text-align: right; }" +
                "    .badge { display: inline-block; padding: 4px 10px; border-radius: 4px; font-size: 11px; font-weight: 700; text-transform: uppercase; }"
                +
                "    .footer { text-align: center; font-size: 11px; color: #94a3b8; margin-top: 60px; line-height: 1.5; }"
                +
                "    .signature-container { margin-top: 40px; width: 100%; border-collapse: collapse; }" +
                "    .signature-box { width: 45%; border-top: 1px dashed #cbd5e1; text-align: center; padding-top: 8px; font-size: 12px; color: #64748b; font-weight: 500; }"
                +
                "    .signature-spacer { width: 10%; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"receipt-container\">" +
                "    <table class=\"header-table\">" +
                "      <tr>" +
                "        <td class=\"logo-title\">" +
                "          <img src=\"src/main/resources/static/ams_no_bg.png\" width=\"32\" height=\"32\" style=\"vertical-align: middle; margin-right: 8px;\" />"
                +
                "          <span style=\"vertical-align: middle;\">AMS ENTERPRISE</span>" +
                "        </td>" +
                "        <td class=\"invoice-label\">RECEIPT</td>" +
                "      </tr>" +
                "    </table>" +
                "    <div class=\"divider\"></div>" +
                "    <table class=\"meta-table\">" +
                "      <tr>" +
                "        <td class=\"meta-col\">" +
                "          <div class=\"meta-title\">ISSUED TO</div>" +
                "          <strong>" + employeeName + "</strong><br />" +
                "          Email: " + employeeEmail + "<br />" +
                "          Company: AMS Enterprise Ltd" +
                "        </td>" +
                "        <td class=\"meta-col\" style=\"text-align: right;\">" +
                "          <div class=\"meta-title\">TRANSACTION DETAILS</div>" +
                "          Receipt ID: #" + transactionId + "<br />" +
                "          Date Processed: " + formatDateTime(date) + "<br />" +
                "          Status: <span style=\"color: " + statusColor + "; font-weight: 700;\">" + actionStatus
                + "</span>" +
                "        </td>" +
                "      </tr>" +
                "    </table>" +
                "    <div class=\"detail-card\">" +
                "      <table class=\"detail-table\">" +
                "        <tr>" +
                "          <td class=\"label\">Asset Name</td>" +
                "          <td class=\"value\">" + assetName + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td class=\"label\">Asset Code</td>" +
                "          <td class=\"value\">" + assetCode + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td class=\"label\">Transaction Type</td>" +
                "          <td class=\"value\"><span class=\"badge\" style=\"background-color: " + statusBg
                + "; color: " + statusColor + ";\">" + actionType + "</span></td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    <!-- DOTTED CUT-OFF SLIP -->" +
                "    <div style=\"margin: 35px 0 25px 0; border-top: 1.5px dashed #cbd5e1; position: relative; text-align: left;\">"
                +
                "      <span style=\"position: absolute; top: -10px; left: 10%; background: #ffffff; padding: 0 8px; font-size: 12px; color: #64748b; font-family: 'Helvetica Neue', Arial, sans-serif; font-weight: 600; letter-spacing: 0.5px;\">&#9986; TEAR ALONG DOTTED LINE FOR IT COMPLIANCE SLIP</span>"
                +
                "    </div>" +
                "    <table class=\"signature-container\">" +
                "      <tr>" +
                "        <td class=\"signature-box\" style=\"width: 40%; border-top: none; text-align: left; vertical-align: bottom;\">"
                +
                "          <div style=\"height: 40px; border-bottom: 1px dotted #cbd5e1; margin-bottom: 8px;\"></div>" +
                "          <strong style=\"color: #0f172a;\">Authorized IT Administrator</strong>" +
                "          <div style=\"font-size: 10px; color: #94a3b8; margin-top: 4px;\">Sign Date: ____/____/______</div>"
                +
                "        </td>" +
                "        <td class=\"signature-spacer\" style=\"width: 20%; text-align: center; vertical-align: middle;\">"
                +
                "          <div style=\"border: 2px dashed #0B4FA0; color: #0B4FA0; font-size: 9px; font-weight: 800; text-transform: uppercase; padding: 4px 6px; border-radius: 4px; transform: rotate(-10deg); display: inline-block; opacity: 0.8; letter-spacing: 0.5px; font-family: 'Courier New', Courier, monospace; margin: 0 auto;\">IT DEPT<br/>APPROVED</div>"
                +
                "        </td>" +
                "        <td class=\"signature-box\" style=\"width: 40%; border-top: none; text-align: left; vertical-align: bottom;\">"
                +
                "          <div style=\"height: 40px; border-bottom: 1px dotted #cbd5e1; margin-bottom: 8px;\"></div>" +
                "          <strong style=\"color: #0f172a;\">Receiver Signature</strong>" +
                "          <div style=\"font-size: 10px; color: #94a3b8; margin-top: 4px;\">Sign Date: ____/____/______</div>"
                +
                "        </td>" +
                "      </tr>" +
                "    </table>" +
                "    <div style=\"border-top: 1px solid #cbd5e1; margin-top: 30px; padding-top: 15px; text-align: justify; font-family: 'Helvetica Neue', Arial, sans-serif;\">"
                +
                "      <p style=\"font-size: 9px; line-height: 1.4; color: #94a3b8; margin: 0;\">" +
                "        <strong>Asset Responsibility:</strong> By signing above, the receiver acknowledges responsibility for the care, security, and professional usage of this corporate asset. The asset remains the property of AMS Enterprise Ltd and must be returned upon request or employment termination."
                +
                "      </p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>This is a system-generated compliance document. Digital copy automatically archived in IT records.</p>"
                +
                "      <p>&#169; " + java.time.Year.now().getValue() + " AMS Enterprise Ltd. All rights reserved.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    public static String formatDateTime(String dateStr) {
        try {
            if (dateStr == null)
                return "N/A";
            String cleaned = dateStr.replace("T", " ");
            java.time.format.DateTimeFormatter inputFormatter = java.time.format.DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss");
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(cleaned.substring(0, 19), inputFormatter);
            java.time.format.DateTimeFormatter outputFormatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd MMM yyyy, hh:mm a");
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return dateStr;
        }
    }
}
