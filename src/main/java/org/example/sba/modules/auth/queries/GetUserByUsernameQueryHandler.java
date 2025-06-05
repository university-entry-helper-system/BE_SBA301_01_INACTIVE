package org.example.sba.modules.auth.queries;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.AccountDocument;
import org.example.sba.repository.AccountMongoRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByUsernameQueryHandler {
    private final AccountMongoRepository accountMongoRepository;

    public AccountDocument handle(GetUserByUsernameQuery query) {
        return accountMongoRepository.findAll().stream()
                .filter(acc -> acc.getUsername().equals(query.getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in MongoDB"));
    }
} 