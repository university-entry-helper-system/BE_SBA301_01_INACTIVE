package org.example.sba.service;

import org.example.sba.model.RedisToken;

public interface RedisTokenService {

    void save(RedisToken token);

    void remove(String id);

    boolean isExists(String id);
}
