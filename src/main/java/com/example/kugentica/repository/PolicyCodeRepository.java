package com.example.kugentica.repository;

import com.example.kugentica.entity.PolicyCode;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyCodeRepository
    extends MongoRepository<PolicyCode, String> {
  java.util.List<PolicyCode> findByPlcyKywdNmContaining(String keyword);
  @org.springframework.data.mongodb.repository.
  Query("{ 'plcyKywdNm': { $regex: ?0, $options: 'i' } }")
  java.util.List<PolicyCode> findByPlcyKywdNmRegex(String keyword);
  Optional<PolicyCode> findById(ObjectId id);

  @org.springframework.data.mongodb.repository.
  Query("{ 'plcyTitle': { $regex: ?0, $options: 'i' } }")
  java.util.List<PolicyCode> findByPlcyTitleRegex(String title);

  @org.springframework.data.mongodb.repository.
  Query("{ 'plcyExplnCn': { $regex: ?0, $options: 'i' } }")
  java.util.List<PolicyCode> findByPlcyExplnCnRegex(String description);

  @org.springframework.data.mongodb.repository.
  Query("{ $or: [ { 'plcyTitle': { $regex: ?0, $options: 'i' } }, { "
        + "'plcyKywdNm': { $regex: ?0, $options: 'i' } } ] }")
  java.util.List<PolicyCode>
  searchByTitleOrKeyword(String query);
}
