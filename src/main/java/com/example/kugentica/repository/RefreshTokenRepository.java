package com.example.kugentica.repository;

import com.example.kugentica.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository
    extends KeyValueRepository<RefreshToken, String> {
  Optional<RefreshToken> findByToken(String token);
  void deleteByToken(String token);

  Boolean existsByRefresh(String refresh);
  void deleteByRefresh(String refresh);
}
