package com.example.kugentica.repository;

import com.example.kugentica.entity.User;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
}
