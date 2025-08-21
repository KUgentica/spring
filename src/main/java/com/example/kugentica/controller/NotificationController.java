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
  NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/fcmToken/save")
  public ResponseEntity saveFcmToken(@RequestBody FCMTokenSaveDTO request) {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    if (username == null || username.equalsIgnoreCase("anonymousUser")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return notificationService.saveFcmToken(request, username);
  }
}
