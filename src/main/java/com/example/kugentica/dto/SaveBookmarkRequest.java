package com.example.kugentica.dto;

import lombok.Data;
import org.bson.types.ObjectId;

// Flutter의 saveBookmark(policy)가 보내는 JSON 본문을 받기 위한 DTO
@Data
public class SaveBookmarkRequest {
    private ObjectId itemId;
    private String itemType;
    private String title;
    private String description;
    // Policy 모델의 다른 필드들도 필요에 따라 추가할 수 있습니다.
}
