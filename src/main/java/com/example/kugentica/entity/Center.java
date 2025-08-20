package com.example.kugentica.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "youth_center")
public class Center {
    @Id
    private String id;
    private String cntrSn;
    private String cntrNm;
    private String cntrAddr;
    private String cntrDaddr;
    private String cntrTelno;
    private String cntrUrlAddr; // 센터 URL 주소
}
