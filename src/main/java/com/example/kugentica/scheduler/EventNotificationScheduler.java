package com.example.kugentica.scheduler;

import com.example.kugentica.entity.CalendarEvent;
import com.example.kugentica.entity.FCMToken;
import com.example.kugentica.repository.CalendarEventRepository;
import com.example.kugentica.repository.FCMTokenRepository;
import com.example.kugentica.service.FirebaseMessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventNotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventNotificationScheduler.class);

    private final CalendarEventRepository calendarEventRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final FirebaseMessagingService firebaseMessagingService;

    public EventNotificationScheduler(CalendarEventRepository calendarEventRepository,
                                      FCMTokenRepository fcmTokenRepository,
                                      FirebaseMessagingService firebaseMessagingService) {
        this.calendarEventRepository = calendarEventRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    /**
     * 매일 오전 9시에 실행됩니다. (cron = "초 분 시 일 월 요일")
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDeadlineReminders() {
        logger.info("마감일 알림 스케줄러를 시작합니다...");

        // 1. 내일이 마감일인 모든 이벤트를 조회합니다.
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<CalendarEvent> events = calendarEventRepository.findByEventDate(tomorrow); // Repository에 해당 메소드 필요

        if (events.isEmpty()) {
            logger.info("내일 마감되는 이벤트가 없습니다.");
            return;
        }

        logger.info("총 {}개의 이벤트에 대한 알림을 보냅니다.", events.size());

        for (CalendarEvent event : events) {
            // 2. 이벤트의 소유자(User) 정보를 가져옵니다.
            //    (CalendarEvent 엔티티에 User와의 관계가 매핑되어 있어야 함)
            String userId = event.getUserId(); // 또는 getEmail()

            // 3. 사용자의 FCM 토큰을 DB에서 조회합니다.
            fcmTokenRepository.findByUserId(userId).ifPresent(fcmToken -> {
                String title = "마감일 알림 잊지 않으셨나요? 👀";
                String body = String.format("'%s' 이벤트가 내일 마감됩니다!", event.getTitle());

                // 4. 알림 전송 서비스 호출
                firebaseMessagingService.sendNotification(fcmToken.getFcmToken(), title, body);
            });
        }
        logger.info("마감일 알림 스케줄러를 종료합니다.");
    }
}

