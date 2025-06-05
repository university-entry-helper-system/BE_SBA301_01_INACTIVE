package org.example.sba.repository;

import org.example.sba.model.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountMongoRepository extends MongoRepository<AccountDocument, String> {
} 