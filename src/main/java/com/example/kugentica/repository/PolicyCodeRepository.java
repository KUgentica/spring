package com.example.kugentica.repository;

import com.example.kugentica.entity.PolicyCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PolicyCodeRepository extends MongoRepository<PolicyCode, String> {
    java.util.List<PolicyCode> findByPlcyKywdNmContaining(String keyword);
    @org.springframework.data.mongodb.repository.Query("{ 'plcyKywdNm': { $regex: ?0, $options: 'i' } }")
    java.util.List<PolicyCode> findByPlcyKywdNmRegex(String keyword);
    Optional<PolicyCode> findById(ObjectId id);
    
    // 제목으로 검색
    @org.springframework.data.mongodb.repository.Query("{ 'plcyTitle': { $regex: ?0, $options: 'i' } }")
    java.util.List<PolicyCode> findByPlcyTitleRegex(String title);
    
    // 설명으로 검색
    @org.springframework.data.mongodb.repository.Query("{ 'plcyExplnCn': { $regex: ?0, $options: 'i' } }")
    java.util.List<PolicyCode> findByPlcyExplnCnRegex(String description);

    // 제목 또는 키워드에서 OR 검색 (설명은 제외해 결과 과다 방지)
    @org.springframework.data.mongodb.repository.Query("{ $or: [ { 'plcyTitle': { $regex: ?0, $options: 'i' } }, { 'plcyKywdNm': { $regex: ?0, $options: 'i' } } ] }")
    java.util.List<PolicyCode> searchByTitleOrKeyword(String query);
}
