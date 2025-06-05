package org.example.sba.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "accounts")
public class AccountDocument {
    @Id
    private String id; // ObjectId dạng String
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    // ... các trường khác cần thiết cho read
} 