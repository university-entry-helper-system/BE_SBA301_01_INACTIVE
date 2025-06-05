package org.example.sba.modules.auth.queries;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserByUsernameQuery {
    private String username;
} 