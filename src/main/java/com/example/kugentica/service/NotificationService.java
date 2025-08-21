package com.example.kugentica.service;

import com.example.kugentica.controller.NotificationController;
import com.example.kugentica.dto.FCMTokenSaveDTO;
import com.example.kugentica.entity.FCMToken;
import com.example.kugentica.repository.FCMTokenRepository;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final FCMTokenRepository fcmTokenRepository;

  NotificationService(FCMTokenRepository fcmTokenRepository) {
    this.fcmTokenRepository = fcmTokenRepository;
  }

  public ResponseEntity saveFcmToken(FCMTokenSaveDTO fcmTokenSaveDTO,
                                     String email) {
    Optional<FCMToken> existingToken = fcmTokenRepository.findByUserId(email);

    FCMToken fcmToken;
    if (existingToken.isPresent()) {
      fcmToken = existingToken.get();
      fcmToken.setFcmToken(fcmTokenSaveDTO.getFcmToken());
    } else {
      fcmToken = new FCMToken();
      fcmToken.setFcmToken(fcmTokenSaveDTO.getFcmToken());
      fcmToken.setUserId(email);
    }

    fcmTokenRepository.save(fcmToken);
    return ResponseEntity.ok(200);
  }
}
