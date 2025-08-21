package com.example.kugentica.repository;

import com.example.kugentica.entity.CalendarEvent;
import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CalendarEventRepository
    extends MongoRepository<CalendarEvent, String> {

  List<CalendarEvent> findByEventDateBetweenAndUserId(LocalDate startDate,
                                                      LocalDate endDate,
                                                      String userId);
  void deleteByIdAndUserId(ObjectId policyId, String userId);
  List<CalendarEvent> findByEventDate(LocalDate deadLine);

  boolean existsByUserIdAndPolicyId(String userId, ObjectId policyId);

  void deleteByUserIdAndPolicyId(String userId, ObjectId policyId);
}