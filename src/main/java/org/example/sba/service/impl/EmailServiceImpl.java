package org.example.sba.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public void sendRegistrationConfirmationEmail(Account account, String confirmationCode) {
        String subject = "Please Confirm Your Registration";

        // Nội dung email với thông tin người dùng và mã xác nhận
        String body = "<html><body>" +
                "<h3>Hello " + account.getFirstName() + " " + account.getLastName() + ",</h3>" +
                "<p>Thank you for registering with us. Please click the link below to confirm your registration:</p>" +
                "<p><a href='http://localhost:8080/account/confirm?email=" + account.getEmail() +
                "&code=" + confirmationCode + "'>Confirm Registration</a></p>" +
                "<p>If you did not register for an account, please ignore this email.</p>" +
                "<p>Best regards,<br>The Team</p>" +
                "</body></html>";

        try {
            // Tạo và cấu hình email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(account.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true); // HTML email
            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            // In ra lỗi và ném ngoại lệ nếu có vấn đề trong quá trình gửi email
            e.printStackTrace();
            throw new RuntimeException("Error sending confirmation email", e);
        }
    }

    @Override
    public void sendActivationEmail(String to, String activationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Activate your account");
        message.setText("Please click the following link to activate your account:\n" +
                "http://localhost:8080/auth/activate?token=" + activationToken);
        mailSender.send(message);
    }
}
