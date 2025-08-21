package com.example.kugentica.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat")
@Getter
@Setter
@AllArgsConstructor
public class Chat {

  @Id private ObjectId chatId;

  private String userEmail;
  private String folder;
  private String role;
  private String text;
  private LocalDateTime timestamp;

  public Chat() { this.timestamp = LocalDateTime.now(); }

  public Chat(String userEmail, String folder, String role, String text) {
    this.userEmail = userEmail;
    this.folder = folder;
    this.role = role;
    this.text = text;
    this.timestamp = LocalDateTime.now();
  }

  public static Builder builder() { return new Builder(); }

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

    public Chat build() { return new Chat(userEmail, folder, role, text); }
  }
}
