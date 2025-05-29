package org.example.sba.service;

import org.example.sba.model.Account;

public interface EmailService {
    void sendRegistrationConfirmationEmail(Account account, String confirmationCode);
}
