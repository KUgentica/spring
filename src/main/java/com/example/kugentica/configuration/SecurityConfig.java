package com.example.kugentica.configuration;

import com.example.kugentica.jwt.CustomLogoutFilter;
import com.example.kugentica.jwt.JwtAuthenticationFilter;
import com.example.kugentica.jwt.JwtTokenProvider;
import com.example.kugentica.jwt.LoginFilter;
import com.example.kugentica.repository.FCMTokenRepository;
import com.example.kugentica.repository.RefreshTokenRepository;
import com.example.kugentica.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableSpringDataWebSupport(
    pageSerializationMode =
        EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SecurityConfig {
  private final CustomUserDetailsService customUserDetailsService;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final FCMTokenRepository fcmTokenRepository;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                        AuthenticationConfiguration authenticationConfiguration,
                        RefreshTokenRepository refreshTokenRepository,
                        JwtTokenProvider jwtTokenProvider,
                        FCMTokenRepository fcmTokenRepository) {
    this.customUserDetailsService = customUserDetailsService;
    this.authenticationConfiguration = authenticationConfiguration;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.fcmTokenRepository = fcmTokenRepository;
  }

  @Bean
  public AuthenticationManager
  authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods(
            "GET", "POST", "PUT", "DELETE", "OPTIONS");
      }
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf((auth) -> auth.disable());
    http.formLogin((auth) -> auth.disable());
    http.httpBasic((auth) -> auth.disable());
    http.addFilterBefore(new CustomLogoutFilter(jwtTokenProvider,
                                                refreshTokenRepository,
                                                fcmTokenRepository),
                         LogoutFilter.class);
    http.addFilterAfter(
        new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
        UsernamePasswordAuthenticationFilter.class);
    http.authorizeHttpRequests(
        (auth)
            -> auth.requestMatchers("/login", "/", "/user/register",
                                    "/user/check-email", "/reissue",
                                    "/user/profile/**", "/chat/**",
                                    "/policy/**", "/center")
                   .permitAll()
                   .anyRequest()
                   .authenticated());

        new LoginFilter(authenticationManager(authenticationConfiguration),
                        jwtTokenProvider, refreshTokenRepository),
        UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement((session)
                                   -> session.sessionCreationPolicy(
                                       (SessionCreationPolicy.STATELESS)));

        return http.build();
  }
}
