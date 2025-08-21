package com.example.kugentica.jwt;

import com.example.kugentica.dto.CustomUserDetails;
import com.example.kugentica.entity.RefreshToken;
import com.example.kugentica.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  public LoginFilter(AuthenticationManager authenticationManager,
                     JwtTokenProvider jwtTokenProvider,
                     RefreshTokenRepository refreshTokenRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenRepository = refreshTokenRepository;
  }
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) {
    String userEmail = null;
    String password = null;

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(request.getInputStream());

      System.out.println("받은 JSON 데이터: " + jsonNode.toString());

      userEmail = jsonNode.get("email").asText();
      password = jsonNode.get("password").asText();

      System.out.println("추출된 이메일: " + userEmail);
      System.out.println("추출된 비밀번호: " + password);

    } catch (IOException e) {
      e.printStackTrace();
    }

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userEmail, password, null);
    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authentication) {
    CustomUserDetails customUserDetails =
        (CustomUserDetails)authentication.getPrincipal();

    String userEmail = authentication.getName();
    String accessToken =
        jwtTokenProvider.createJwt("access", userEmail, 600000L);
    String refreshToken =
        jwtTokenProvider.createJwt("refresh", userEmail, 86400000L);

    addRefreshEntity(customUserDetails.getUserId(), refreshToken);

    response.setHeader("access", accessToken);
    response.addCookie(createCookie("refresh", refreshToken));
    response.setStatus(HttpStatus.OK.value());
  }
  private void addRefreshEntity(ObjectId userId, String refreshToken) {

    RefreshToken refreshEntity = new RefreshToken(refreshToken, userId);

    refreshTokenRepository.save(refreshEntity);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) {
    response.setStatus(401);
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);

    return cookie;
  }
}
