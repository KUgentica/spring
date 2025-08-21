package com.example.kugentica.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 86400)
@Getter
@NoArgsConstructor
public class RefreshToken {
  @Id private String token;

  private ObjectId userId;

  public RefreshToken(String token, ObjectId userId) {
    this.token = token;
    this.userId = userId;
  }
}