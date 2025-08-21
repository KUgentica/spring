package com.example.kugentica.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "youth_policy")
public class PolicyCode {
  @Id private String id;
  private String plcyId;
  private String plcyTitle;
  private String plcyKywdNm;
  private String plcyExplnCn;
  private String zipCd;
  private String aplyYmd;
  private String earnMinAmt;
  private String earnMaxAmt;
  private String sprtTrgtMinAge;
  private String sprtTrgtMaxAge;
  private String plcyAplyMthdCn;
  private String aplyUrlAddr;
  private String refUrlAddr1;
  private String refUrlAddr2;
}
