package com.example.kugentica.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat")
@Getter
@Setter
@AllArgsConstructor
public class Chat {

    @Id
    private ObjectId chatId;

    private String userEmail;      // 사용자 이메일 (사용자 구분용)
    private String folder;         // 채팅 폴더명 (예: "일반", "창업", "취업" 등)
    private String role;           // 메시지 역할 (user/ai)
    private String text;           // 메시지 내용
    private LocalDateTime timestamp; // 메시지 시간

    public Chat() {
        this.timestamp = LocalDateTime.now();
    }

    public Chat(String userEmail, String folder, String role, String text) {
        this.userEmail = userEmail;
        this.folder = folder;
        this.role = role;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userEmail;
        private String folder;
        private String role;
        private String text;

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder folder(String folder) {
            this.folder = folder;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Chat build() {
            return new Chat(userEmail, folder, role, text);
        }
    }
}
