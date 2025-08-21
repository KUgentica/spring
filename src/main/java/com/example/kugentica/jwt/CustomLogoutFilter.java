package com.example.kugentica.jwt;

import com.example.kugentica.repository.FCMTokenRepository;
import com.example.kugentica.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.GenericFilterBean;

public class CustomLogoutFilter extends GenericFilterBean {
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshtokenRepository;
  private final FCMTokenRepository fcmTokenRepository;

  public CustomLogoutFilter(JwtTokenProvider jwtTokenProvider,
                            RefreshTokenRepository refreshtokenRepository,
                            FCMTokenRepository fcmTokenRepository) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshtokenRepository = refreshtokenRepository;
    this.fcmTokenRepository = fcmTokenRepository;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {

    doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
  }

  private void doFilter(HttpServletRequest request,
                        HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    String requestUri = request.getRequestURI();
    if (!requestUri.matches("^\\/logout$")) {

      filterChain.doFilter(request, response);
      return;
    }
    String requestMethod = request.getMethod();
    if (!requestMethod.equals("POST")) {

      filterChain.doFilter(request, response);
      return;
    }

    String refresh = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {

      if (cookie.getName().equals("refresh")) {

        refresh = cookie.getValue();
      }
    }

    if (refresh == null) {

      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    try {
      jwtTokenProvider.validateToken(refresh);
    } catch (ExpiredJwtException e) {

      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String category = jwtTokenProvider.getCategory(refresh);
    if (!category.equals("refresh")) {

      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Boolean isExist = refreshtokenRepository.existsByRefresh(refresh);
    if (!isExist) {

      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String userId = jwtTokenProvider.getMemberEmail(refresh);

    if (userId != null) {
      fcmTokenRepository.deleteByUserId(userId);
    }

    refreshtokenRepository.deleteByRefresh(refresh);

    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");

    response.addCookie(cookie);
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
