package org.example.sba.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public void sendRegistrationConfirmationEmail(Account account) {
        String subject = "Please Confirm Your Registration";
        String body = "<html><body><h3>Hello " + account.getFirstName() + " " + account.getLastName() + ",</h3>" +
                "<p>Thank you for registering with us. Please click the link below to confirm your registration:</p>" +
                "<a href='http://localhost:8080/account/confirm?email=" + account.getEmail() + "'>Confirm Registration</a>" +
                "</body></html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(account.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true); // HTML email
            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending confirmation email", e);
        }
    }
}
