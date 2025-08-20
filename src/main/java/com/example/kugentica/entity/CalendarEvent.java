package com.example.kugentica.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "calendar_events")
@Data
public class CalendarEvent {
    @Id
    private String id;

    private ObjectId policyId;
    private String title;
    private String category;
    private LocalDate eventDate;

    /**
     * 데이터를 소유한 유저를 식별하는 ID
     */
    @Indexed
    private String userId;
}