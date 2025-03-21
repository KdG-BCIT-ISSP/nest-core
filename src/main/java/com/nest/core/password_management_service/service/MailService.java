package com.nest.core.password_management_service.service;

import org.springframework.stereotype.Service;

import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.password_management_service.exception.EmailNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    // Frontend can maybe adjust what the email could look like to match with the rest of the site
    private static final String emailStart = "NEST - Password Reset Request\n\nHello, someone has requested a password reset for your account.\n\n";
    private static final String emailCode = "Click on this link to reset your password: ";
    private static final String emailEnd = "\n\nThis code will expire in 5 minuntes\n\nIf you did not request this, please ignore this email.\n\nNEST Team";

    public void sendResetToken(String email, String domain, String resetToken) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("noreply@nest.com");
        mail.setTo(email);
        mail.setSubject("NEST - Password Reset Request");
        mail.setText(emailStart + emailCode + domain + "/" + resetToken + emailEnd);
        emailSender.send(mail);

        log.info("Reset token sent to {}", email);
    }
}
