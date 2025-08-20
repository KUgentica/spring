package com.example.kugentica.repository;

import com.example.kugentica.entity.Bookmark;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends MongoRepository<Bookmark, String> {
    // --- ⭐️ 변경 및 추가된 메서드 ---
    Optional<Bookmark> findByUserIdAndItemId(String userId, ObjectId itemId);
    boolean existsByUserIdAndItemId(String userId, ObjectId itemId);
    void deleteByUserIdAndItemId(String userId, ObjectId itemId);

    // 정렬 로직은 그대로 사용
    List<Bookmark> findByUserId(String userId, Sort sort);
}