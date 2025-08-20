package com.example.kugentica.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 86400)  // 24시간(초 단위)
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String token;    // Redis key

    private ObjectId userId;   // FK 역할

    public RefreshToken(String token, ObjectId userId) {
        this.token    = token;
        this.userId = userId;
    }
}