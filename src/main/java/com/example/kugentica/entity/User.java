package com.example.kugentica.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "user")
@Getter
@Setter
@AllArgsConstructor
public class User {

  @Id private ObjectId userId;

  private String email;

  private String nickName;

  private String password;

  private String region = null;

  private Integer age = null;

  private String gender = null;

  private User(String email, String nickName, String password, String region,
               Integer age, String gender) {
    this.email = email;
    this.nickName = nickName;
    this.password = password;
    this.region = region;
    this.age = age;
    this.gender = gender;
  }

  public User() {}

  public static Builder builder() { return new Builder(); }

  public static class Builder {
    private String email;
    private String nickName;
    private String password;
    private String region;
    private Integer age;
    private String gender;

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder nickName(String nickName) {
      this.nickName = nickName;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder region(String region) {
      this.region = region;
      return this;
    }

    public Builder age(Integer age) {
      this.age = age;
      return this;
    }

    public Builder gender(String gender) {
      this.gender = gender;
      return this;
    }

    public User build() {
      return new User(email, nickName, password, null, null, null);
    }
  }
}
