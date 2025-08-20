package com.example.kugentica.repository;

import com.example.kugentica.entity.CalendarEvent;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends MongoRepository<CalendarEvent, String> {

    // 특정 유저의 특정 기간 이벤트를 조회합니다.
    List<CalendarEvent> findByEventDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, String userId);

    // 특정 유저의 특정 정책 관련 이벤트를 삭제합니다.
    void deleteByIdAndUserId(ObjectId policyId, String userId);
    List<CalendarEvent> findByEventDate(LocalDate deadLine);

}