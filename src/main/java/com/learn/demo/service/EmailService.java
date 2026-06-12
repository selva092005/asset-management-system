package com.learn.demo.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String htmlContent);
}
