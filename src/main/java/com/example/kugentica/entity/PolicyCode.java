package com.example.kugentica.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "youth_policy")
public class PolicyCode {
    @Id
    private String id;
    private String plcyId;
    private String plcyTitle;
    private String plcyKywdNm; // 정책키워드명
    private String plcyExplnCn; // 정책설명내용
    private String zipCd; // 정책거주지역코드
    private String aplyYmd; // 신청기간
    // 필요한 필드 추가 가능
    private String earnMinAmt; // 소득최소금액
    private String earnMaxAmt; // 소득최대금액
    private String sprtTrgtMinAge; // 지원대상최소연령
    private String sprtTrgtMaxAge; // 지원대상최대연령
    private String plcyAplyMthdCn; // 정책신청방법내용
    private String aplyUrlAddr; // 신청 URL 주소
    private String refUrlAddr1; // 참고 URL 주소1
    private String refUrlAddr2; // 참고 URL 주소2
}
