package com.example.kugentica.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "calendar_events")
@org.springframework.data.mongodb.core.index.
CompoundIndex(def = "{'userId': 1, 'policyId': 1}", unique = true)
@Data
public class CalendarEvent {
  @Id private String id;

  private ObjectId policyId;
  private String title;
  private String category;
  private LocalDate eventDate;

  private LocalDateTime createdAt;

  @Indexed private String userId;
}
