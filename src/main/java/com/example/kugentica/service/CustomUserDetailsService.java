package com.example.kugentica.service;

import com.example.kugentica.repository.UserRepository;
import com.example.kugentica.dto.CustomUserDetails;
import com.example.kugentica.entity.User;
import com.example.kugentica.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 사용자 조회 시도: " + email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ 사용자를 찾을 수 없음: " + email);
                    return new UsernameNotFoundException("No user found with email: " + email);
                });
        
        System.out.println("✅ 사용자 조회 성공: " + user.getEmail() + ", ID: " + user.getUserId());
        return new CustomUserDetails(user);
    }

}