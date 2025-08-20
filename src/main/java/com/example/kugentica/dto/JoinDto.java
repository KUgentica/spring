package com.example.kugentica.dto;

import lombok.Getter;

@Getter
public class JoinDto {
    private String email;
    private String nickname;
    private String password;
    private String region;  // 지역 (국가 코드)
    private Integer age;     // 나이
    private String gender;   // 성별
}
