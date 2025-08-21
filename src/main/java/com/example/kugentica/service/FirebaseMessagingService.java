package com.example.kugentica.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

  private static final Logger logger =
      LoggerFactory.getLogger(FirebaseMessagingService.class);

  /**
   * @param token 대상 디바이스 토큰
   * @param title 알림 제목
   * @param body  알림 내용
   */

  public void sendNotification(String token, String title, String body) {
    if (token == null || token.isEmpty()) {
      logger.warn("FCM 토큰이 비어있어 알림을 보낼 수 없습니다.");
      return;
    }

    Notification notification =
        Notification.builder().setTitle(title).setBody(body).build();

    Message message =
        Message.builder().setToken(token).setNotification(notification).build();

    try {
      String response = FirebaseMessaging.getInstance().send(message);
      logger.info("성공적으로 알림을 보냈습니다. Message ID: {}", response);
    } catch (FirebaseMessagingException e) {
      logger.error("알림 전송에 실패했습니다.", e);
    }
  }
}