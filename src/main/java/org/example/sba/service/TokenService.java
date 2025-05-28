package org.example.sba.service;

import org.example.sba.model.Token;

public interface TokenService {

    Token getTokenByUsername(String username);

    int saveToken(Token token);

    void deleteTokenByUsername(String username);
}
