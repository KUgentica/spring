package com.example.kugentica.repository;

import com.example.kugentica.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookmarkRepository extends MongoRepository<Bookmark, String> {
  Optional<Bookmark> findByUserIdAndItemId(String userId, ObjectId itemId);
  boolean existsByUserIdAndItemId(String userId, ObjectId itemId);
  void deleteByUserIdAndItemId(String userId, ObjectId itemId);

  List<Bookmark> findByUserId(String userId, Sort sort);
}