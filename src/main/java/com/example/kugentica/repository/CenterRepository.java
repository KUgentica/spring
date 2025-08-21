package com.example.kugentica.repository;
import com.example.kugentica.entity.Center;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CenterRepository extends MongoRepository<Center, ObjectId> {
  List<Center> findAll();
  Center findByCntrSn(String cntrSn);

  // 센터명으로 검색
  @org.springframework.data.mongodb.repository.
  Query("{ 'cntrNm': { $regex: ?0, $options: 'i' } }")
  List<Center> findByCntrNmRegex(String name);

  // 주소로 검색
  @org.springframework.data.mongodb.repository.
  Query("{ 'cntrAddr': { $regex: ?0, $options: 'i' } }")
  List<Center> findByCntrAddrRegex(String address);

  // 상세주소로 검색
  @org.springframework.data.mongodb.repository.
  Query("{ 'cntrDaddr': { $regex: ?0, $options: 'i' } }")
  List<Center> findByCntrDaddrRegex(String detailAddress);
}
