package com.example.kugentica.dto;

import com.example.kugentica.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookmarkDetailResponse {
  private Bookmark bookmark;
  private Object detailItem;
}