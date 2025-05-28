package org.example.sba.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.exception.InvalidDataException;
import org.example.sba.model.RedisToken;
import org.example.sba.repository.RedisTokenRepository;
import org.example.sba.service.RedisTokenService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    @Override
    public void save(RedisToken token) {
        redisTokenRepository.save(token);
    }

    @Override
    public void remove(String id) {
        isExists(id);
        redisTokenRepository.deleteById(id);
        log.info("Token has deleted successfully, id={}", id);
    }

    @Override
    public boolean isExists(String id) {
        if (!redisTokenRepository.existsById(id)) {
            throw new InvalidDataException("Token not exists");
        }
        return true;
    }
}
