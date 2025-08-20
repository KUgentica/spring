package com.example.kugentica.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fcmToken")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FCMToken {

    @Id
    private String id;
    @Indexed
    private String userId;

    private String fcmToken;

}
