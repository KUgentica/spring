package com.example.kugentica.controller;

import com.example.kugentica.dto.JoinDto;
import com.example.kugentica.service.JoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@ResponseBody
@RequestMapping("/user")
public class JoinController {
    private final JoinService joinService;


    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @GetMapping("/checkId")
    public ResponseEntity<Void> validateMemberId(@RequestParam String id) {
        if (joinService.isIdDuplicate(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
    
    @PostMapping("/check-email")
    public ResponseEntity<Object> checkEmailDuplicate(@RequestBody JoinDto joinDto) {
        boolean isDuplicate = joinService.isEmailDuplicate(joinDto.getEmail());
        return ResponseEntity.ok()
            .body(Map.of("duplicate", isDuplicate));
    }
    
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody JoinDto joinDto){
        return joinService.registerUser(joinDto);
    }



}
