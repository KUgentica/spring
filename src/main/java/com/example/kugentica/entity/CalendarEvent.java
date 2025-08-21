package com.example.kugentica.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Document(collection = "calendar_events")
@org.springframework.data.mongodb.core.index.CompoundIndex(
    def = "{'userId': 1, 'policyId': 1}",
    unique = true
)
@Data
public class CalendarEvent {
    @Id
    private String id;

    private ObjectId policyId;
    private String title;
    private String category;
    private LocalDate eventDate;
    
    /**
     * UTC 기준 생성 시간 (디버깅 및 UTC 시간대 문제 해결용)
     */
    private LocalDateTime createdAt;

    /**
     * 데이터를 소유한 유저를 식별하는 ID
     */
    @Indexed
    private String userId;
}
