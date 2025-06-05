package org.example.sba.modules.account.queries;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.AccountDocument;
import org.example.sba.repository.AccountMongoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAccountQueryHandler {
    private final AccountMongoRepository accountMongoRepository;

    public List<AccountDocument> handle(ListAccountQuery query) {
        return accountMongoRepository.findAll(PageRequest.of(query.getPage(), query.getSize())).getContent();
    }
} 