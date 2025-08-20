package com.example.kugentica.entity;

import com.example.kugentica.dto.SaveBookmarkRequest;
import lombok.AllArgsConstructor;
import lombok.Data; // Getter, Setter 등 보일러플레이트 코드를 줄여주는 Lombok
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "bookmarks")
@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor를 합친 어노테이션
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 포함하는 생성자 추가
public class Bookmark {

    @Id
    private String id;


    private String userId; // 이 즐겨찾기를 소유한 사용자의 ID


    private boolean isPinned = false;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    private ObjectId itemId;     // 정책 ID 또는 센터 ID

    private String itemType;   // "POLICY" 또는 "CENTER"

    private String title;
    private String description;



    // 기본 생성자
    public Bookmark(String userId, SaveBookmarkRequest item, boolean b) {
        this.userId = userId;
        this.itemId = item.getItemId();
        this.itemType = item.getItemType();
        this.title = item.getTitle();
        this.isPinned = b;
        this.description = item.getDescription();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


}
