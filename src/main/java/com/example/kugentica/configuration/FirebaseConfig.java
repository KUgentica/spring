package com.example.kugentica.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private static final String FIREBASE_CONFIG_PATH = "firebase-service-account-key.json";

    @PostConstruct
    public void initialize() {
        try {
            // 클래스패스에서 서비스 계정 키 파일을 읽어옵니다.
            InputStream serviceAccount = new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // FirebaseApp이 이미 초기화되지 않았을 경우에만 초기화를 진행합니다.
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("FirebaseApp 초기화가 성공적으로 완료되었습니다.");
            }
        } catch (IOException e) {
            logger.error("FirebaseApp 초기화 중 오류 발생", e);
        }
    }
}