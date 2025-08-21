package com.example.kugentica.jwt;

import com.example.kugentica.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;

  private final CustomUserDetailsService customUserDetailsService;
  public JwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider,
      CustomUserDetailsService customUserDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    String accessToken = header.substring(7);
    try {
      jwtTokenProvider.validateToken(accessToken);
    } catch (ExpiredJwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("text/plain");
      response.getWriter().write("ACCESS TOKEN EXPIRED");
      return;
    } catch (JwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("text/plain");
      response.getWriter().write("INVALID ACCESS TOKEN");
      return;
    }

    if (!"access".equals(jwtTokenProvider.getCategory(accessToken))) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("text/plain");
      response.getWriter().write("INVALID ACCESS TOKEN TYPE");
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String email = jwtTokenProvider.getMemberEmail(accessToken);
      UserDetails userDetails =
          customUserDetailsService.loadUserByUsername(email);
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(userDetails, null,
                                                  userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }
}
