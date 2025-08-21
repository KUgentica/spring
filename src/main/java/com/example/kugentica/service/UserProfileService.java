package com.example.kugentica.service;

import com.example.kugentica.dto.UserProfileDto;
import com.example.kugentica.entity.User;
import com.example.kugentica.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
  private final UserRepository userRepository;

  @Autowired
  public UserProfileService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public ResponseEntity updateUserProfile(String email,
                                          UserProfileDto profileDto) {
    try {
      System.out.println("사용자 프로필 업데이트 시도");
      System.out.println("이메일: " + email);
      System.out.println("지역: " + profileDto.getRegion());
      System.out.println("나이: " + profileDto.getAge());
      System.out.println("성별: " + profileDto.getGender());

      Optional<User> userOptional = userRepository.findByEmail(email);
      if (userOptional.isEmpty()) {
        System.out.println("사용자를 찾을 수 없음: " + email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("사용자를 찾을 수 없습니다.");
      }

      User user = userOptional.get();

      user.setRegion(profileDto.getRegion());
      user.setAge(profileDto.getAge());
      user.setGender(profileDto.getGender());

      System.out.println("프로필 정보 저장 시도...");
      User savedUser = userRepository.save(user);
      System.out.println("프로필 정보 저장 완료!");
      System.out.println("업데이트된 User ID: " + savedUser.getUserId());

      return ResponseEntity.ok().build();

    } catch (Exception e) {
      System.err.println("프로필 업데이트 중 오류 발생!");
      System.err.println("오류 메시지: " + e.getMessage());
      System.err.println("오류 타입: " + e.getClass().getSimpleName());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("프로필 업데이트 중 오류가 발생했습니다.");
    }
  }

  public ResponseEntity getUserProfile(String email) {
    try {
      System.out.println("사용자 프로필 조회 시도");
      System.out.println("이메일: " + email);

      Optional<User> userOptional = userRepository.findByEmail(email);
      if (userOptional.isEmpty()) {
        System.out.println("사용자를 찾을 수 없음: " + email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("사용자를 찾을 수 없습니다.");
      }

      User user = userOptional.get();

      return ResponseEntity.ok().body(Map.of(
          "email", user.getEmail(), "nickname", user.getNickName(), "region",
          user.getRegion(), "age", user.getAge(), "gender", user.getGender()));

    } catch (Exception e) {
      System.err.println("프로필 조회 중 오류 발생!");
      System.err.println("오류 메시지: " + e.getMessage());
      System.err.println("오류 타입: " + e.getClass().getSimpleName());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("프로필 조회 중 오류가 발생했습니다.");
    }
  }
}
