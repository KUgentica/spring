package com.example.kugentica.repository;

import com.example.kugentica.entity.RefreshToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends KeyValueRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    
    // 기존 메서드명 유지 (호환성)
    Boolean existsByRefresh(String refresh);
    void deleteByRefresh(String refresh);
}
