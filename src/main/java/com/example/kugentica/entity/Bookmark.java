package com.example.kugentica.entity;

import com.example.kugentica.dto.SaveBookmarkRequest;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bookmarks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

  @Id private String id;

  private String userId;

  private boolean isPinned = false;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private ObjectId itemId;

  private String itemType;

  private String title;
  private String description;

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
