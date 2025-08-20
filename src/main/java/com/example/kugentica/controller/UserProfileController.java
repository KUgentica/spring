package com.example.kugentica.controller;

import com.example.kugentica.dto.UserProfileDto;
import com.example.kugentica.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;
    
    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    @PutMapping("/update")
    public ResponseEntity updateProfile(
            @RequestParam String email,
            @RequestBody UserProfileDto profileDto) {
        return userProfileService.updateUserProfile(email, profileDto);
    }
    
    @GetMapping("/get")
    public ResponseEntity getProfile(@RequestParam String email) {
        return userProfileService.getUserProfile(email);
    }
}
