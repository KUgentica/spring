package com.example.kugentica.repository;

import com.example.kugentica.entity.FCMToken;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FCMTokenRepository extends MongoRepository<FCMToken, String> {
    public void deleteByUserId(String userId);
    public Optional<FCMToken> findByUserId(String userId);
}
