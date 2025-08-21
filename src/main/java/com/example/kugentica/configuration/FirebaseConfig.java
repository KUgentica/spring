package com.example.kugentica.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

  private static final Logger logger =
      LoggerFactory.getLogger(FirebaseConfig.class);
  private static final String FIREBASE_CONFIG_PATH =
      "firebase-service-account-key.json";

  @PostConstruct
  public void initialize() {
    try {
      InputStream serviceAccount =
          new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream();

      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
        logger.info("FirebaseApp 초기화가 성공적으로 완료되었습니다.");
      }
    } catch (IOException e) {
      logger.error("FirebaseApp 초기화 중 오류 발생", e);
    }
  }
}