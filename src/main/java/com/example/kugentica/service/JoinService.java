package com.example.kugentica.service;

import com.example.kugentica.entity.User;
import com.example.kugentica.repository.UserRepository;
import com.example.kugentica.dto.JoinDto;
import com.example.kugentica.entity.User;
import com.example.kugentica.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean isIdDuplicate(String memberEmail){
        return userRepository.existsByEmail(memberEmail);
    }
    
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public ResponseEntity registerUser(JoinDto joinDTO){
        try {
            System.out.println("🔐 회원가입 시도 시작");
            System.out.println("📧 이메일: " + joinDTO.getEmail());
            System.out.println("👤 닉네임: " + joinDTO.getNickname());
            System.out.println("🔒 비밀번호 길이: " + joinDTO.getPassword().length());
            
            // 이메일 중복 확인
            System.out.println("🔍 이메일 중복 확인 시작: " + joinDTO.getEmail());
            boolean isDuplicate = userRepository.existsByEmail(joinDTO.getEmail());
            System.out.println("🔍 중복 확인 결과: " + isDuplicate);
            
            if (isDuplicate) {
                System.out.println("❌ 이메일 중복: " + joinDTO.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 사용 중인 이메일입니다.");
            }
            
            System.out.println("✅ 이메일 중복 확인 완료");
            
            // User 객체 생성 (프로필 정보 없이)
            User user = User.builder()
                            .email(joinDTO.getEmail())
                            .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                            .nickName(joinDTO.getNickname())
                            // region, age, gender는 onboarding에서 설정
                            .build();
            
            System.out.println("👤 User 객체 생성 완료");
            System.out.println("🆔 생성된 User ID: " + user.getUserId());
            System.out.println("📧 생성된 User 이메일: " + user.getEmail());
            System.out.println("👤 생성된 User 닉네임: " + user.getNickName());
            
            // MongoDB에 저장
            System.out.println("💾 MongoDB 저장 시도...");
            User savedUser = userRepository.save(user);
            System.out.println("✅ MongoDB 저장 완료!");
            System.out.println("🆔 저장된 User ID: " + savedUser.getUserId());
            System.out.println("📧 저장된 User 이메일: " + savedUser.getEmail());
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            System.err.println("💥 회원가입 중 오류 발생!");
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            System.err.println("❌ 오류 타입: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.ok().build();
        }
    }
}
