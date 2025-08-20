package com.example.kugentica.controller;

import com.example.kugentica.jwt.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public class ReissueController {
    private final JWTService jwtService;
    public ReissueController(JWTService jwtService){
        this.jwtService = jwtService;
    }
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        return jwtService.recreateJwt(request,response);
    }
}
