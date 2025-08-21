package com.example.kugentica.dto;

import com.example.kugentica.entity.User;
import java.util.Collection;
import java.util.Collections;
import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
  private final User user;

  public CustomUserDetails(User user) { this.user = user; }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }
  public String getNickname() { return user.getNickName(); }
  public ObjectId getUserId() { return user.getUserId(); }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }
}
