package com.example.kugentica.dto;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class SaveBookmarkRequest {
  private ObjectId itemId;
  private String itemType;
  private String title;
  private String description;
}
