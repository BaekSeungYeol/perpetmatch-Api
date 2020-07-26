package com.perpetmatch.infra.mail;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
