package com.example.kugentica.jwt;

import com.example.kugentica.entity.RefreshToken;
import com.example.kugentica.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JWTService {
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  public JWTService(JwtTokenProvider jwtTokenProvider,
                    RefreshTokenRepository refreshTokenRepository) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenRepository = refreshTokenRepository;
  }
  public ResponseEntity recreateJwt(HttpServletRequest request,
                                    HttpServletResponse response) {
    String refreshToken = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("refresh")) {
        refreshToken = cookie.getValue();
      }
    }

    if (refreshToken == null) {
      return new ResponseEntity("REFRESH TOKEN IS NULL",
                                HttpStatus.BAD_REQUEST);
    }

    try {
      jwtTokenProvider.validateToken(refreshToken);
    } catch (ExpiredJwtException e) {
      return new ResponseEntity("REFRESH TOKEN EXPIRED",
                                HttpStatus.BAD_REQUEST);
    }

    String category = jwtTokenProvider.getCategory(refreshToken);
    if (!category.equals("refresh")) {
      return new ResponseEntity("INVALID REFRESH TOKEN",
                                HttpStatus.BAD_REQUEST);
    }
    RefreshToken refreshToken1 =
        refreshTokenRepository.findById(refreshToken)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    String userEmail = jwtTokenProvider.getMemberEmail(refreshToken);

    String newAccessToken =
        jwtTokenProvider.createJwt("access", userEmail, 600000L);
    String newRefresh =
        jwtTokenProvider.createJwt("refresh", userEmail, 86400000L);

    refreshTokenRepository.deleteByRefresh(refreshToken);

    addRefreshEntity(refreshToken1.getUserId(), newRefresh);

    response.setHeader("Authorization", newAccessToken);
    response.addCookie(createCookie("refresh", newRefresh));

    return new ResponseEntity(HttpStatus.OK);
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);

    return cookie;
  }

  private void addRefreshEntity(ObjectId userId, String refresh) {
    RefreshToken refreshEntity = new RefreshToken(refresh, userId);
    refreshTokenRepository.save(refreshEntity);
  }
}
