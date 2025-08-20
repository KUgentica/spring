package com.example.kugentica.repository;

import com.example.kugentica.entity.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {
    
    // 사용자별 채팅 메시지 조회 (최신순)
    @Query(value = "{'userEmail': ?0}", sort = "{'timestamp': 1}")
    List<Chat> findByUserEmailOrderByTimestampAsc(String userEmail);
    
    // 사용자별 특정 폴더의 채팅 메시지 조회 (최신순)
    @Query(value = "{'userEmail': ?0, 'folder': ?1}", sort = "{'timestamp': 1}")
    List<Chat> findByUserEmailAndFolderOrderByTimestampAsc(String userEmail, String folder);
    
    // 사용자별 폴더 목록 조회 (중복 제거)
    @Query(value = "{'userEmail': ?0}", fields = "{'folder': 1}")
    List<Chat> findFoldersByUserEmail(String userEmail);
    
    // 사용자별 채팅 메시지 개수
    long countByUserEmail(String userEmail);
    
    // 사용자별 특정 폴더의 채팅 메시지 개수
    long countByUserEmailAndFolder(String userEmail, String folder);
    
    // 사용자별 채팅 메시지 삭제
    void deleteByUserEmail(String userEmail);
    
    // 사용자별 특정 폴더의 채팅 메시지 삭제
    void deleteByUserEmailAndFolder(String userEmail, String folder);
}
