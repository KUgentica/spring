package com.example.kugentica.scheduler;

import com.example.kugentica.entity.CalendarEvent;
import com.example.kugentica.entity.FCMToken;
import com.example.kugentica.repository.CalendarEventRepository;
import com.example.kugentica.repository.FCMTokenRepository;
import com.example.kugentica.service.FirebaseMessagingService;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationScheduler {

  private static final Logger logger =
      LoggerFactory.getLogger(EventNotificationScheduler.class);

  private final CalendarEventRepository calendarEventRepository;
  private final FCMTokenRepository fcmTokenRepository;
  private final FirebaseMessagingService firebaseMessagingService;

  public EventNotificationScheduler(
      CalendarEventRepository calendarEventRepository,
      FCMTokenRepository fcmTokenRepository,
      FirebaseMessagingService firebaseMessagingService) {
    this.calendarEventRepository = calendarEventRepository;
    this.fcmTokenRepository = fcmTokenRepository;
    this.firebaseMessagingService = firebaseMessagingService;
  }

  @Scheduled(cron = "0 0 9 * * *")
  public void sendDeadlineReminders() {
    logger.info("마감일 알림 스케줄러를 시작합니다...");

    LocalDate tomorrow = LocalDate.now().plusDays(1);
    List<CalendarEvent> events =
        calendarEventRepository.findByEventDate(tomorrow);

    if (events.isEmpty()) {
      logger.info("내일 마감되는 이벤트가 없습니다.");
      return;
    }

    logger.info("총 {}개의 이벤트에 대한 알림을 보냅니다.", events.size());

    for (CalendarEvent event : events) {
      String userId = event.getUserId();

      fcmTokenRepository.findByUserId(userId).ifPresent(fcmToken -> {
        String title = "마감일 알림 잊지 않으셨나요?";
        String body =
            String.format("'%s' 이벤트가 내일 마감됩니다!", event.getTitle());

        firebaseMessagingService.sendNotification(fcmToken.getFcmToken(), title,
                                                  body);
      });
    }
    logger.info("마감일 알림 스케줄러를 종료합니다.");
  }
}
