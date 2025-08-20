package com.example.kugentica.dto;

import com.example.kugentica.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookmarkDetailResponse {
    private Bookmark bookmark; // 기본 즐겨찾기 정보
    private Object detailItem; // PolicyCode 또는 Center 등 실제 데이터 객체
}