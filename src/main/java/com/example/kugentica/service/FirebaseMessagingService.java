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

    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessagingService.class);

    /**
     * 특정 기기(FCM 토큰)로 푸시 알림을 보냅니다.
     * @param token 알림을 받을 기기의 FCM 토큰
     * @param title 알림 제목
     * @param body 알림 내용
     */
    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            logger.warn("FCM 토큰이 비어있어 알림을 보낼 수 없습니다.");
            return;
        }

        // 알림 메시지 구성
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // 메시지 객체 생성
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                // 여기에 추가적인 데이터 페이로드를 담을 수 있습니다.
                // .putData("key", "value")
                .build();

        try {
            // Firebase로 메시지 전송 요청
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("성공적으로 알림을 보냈습니다. Message ID: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("알림 전송에 실패했습니다.", e);
            // 여기서 특정 예외(예: 토큰 만료)에 따라 DB에서 토큰을 삭제하는 로직을 추가할 수 있습니다.
        }
    }
}