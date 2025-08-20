package com.example.kugentica.service;

import com.example.kugentica.controller.NotificationController;
import com.example.kugentica.dto.FCMTokenSaveDTO;
import com.example.kugentica.entity.FCMToken;
import com.example.kugentica.repository.FCMTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {
    private final FCMTokenRepository fcmTokenRepository;

    NotificationService(FCMTokenRepository fcmTokenRepository){
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public ResponseEntity saveFcmToken(FCMTokenSaveDTO fcmTokenSaveDTO, String email) {
        // 이미 존재하는 FCM 토큰 조회
        Optional<FCMToken> existingToken = fcmTokenRepository.findByUserId(email);

        FCMToken fcmToken;
        if (existingToken.isPresent()) {
            // 기존 토큰이 있으면 업데이트
            fcmToken = existingToken.get();
            fcmToken.setFcmToken(fcmTokenSaveDTO.getFcmToken());
        } else {
            // 기존 토큰이 없으면 새로 생성
            fcmToken = new FCMToken();
            fcmToken.setFcmToken(fcmTokenSaveDTO.getFcmToken());
            fcmToken.setUserId(email);
        }

        fcmTokenRepository.save(fcmToken);
        return ResponseEntity.ok(200);
    }

}
