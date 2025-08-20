package com.example.kugentica.controller;

import com.example.kugentica.dto.FCMTokenSaveDTO;
import com.example.kugentica.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")

public class NotificationController {
    private final NotificationService notificationService;
    NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @PostMapping("/fcmToken/save")
    public ResponseEntity saveFcmToken(
            @RequestBody FCMTokenSaveDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Authentication 객체에서 사용자 이름(Principal)을 가져옵니다.
        //    JWT 필터에서 Principal로 이메일(또는 username)을 설정해두었습니다.
        String username = authentication.getName();

        // [중요] 인증된 사용자가 없는 경우에 대한 예외 처리
        if (username == null || username.equalsIgnoreCase("anonymousUser")) {
            // 예외를 던지거나, 적절한 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }// 인증된 사용자의 ID를 받습니다.

        return notificationService.saveFcmToken(request,username);
    }
}
