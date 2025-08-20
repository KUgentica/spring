package com.example.kugentica.controller;

import com.example.kugentica.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    // 사용자별 채팅 메시지 조회
    @GetMapping("/get")
    public ResponseEntity getUserChats(@RequestParam String userEmail) {
        return chatService.getUserChats(userEmail);
    }
    
    // 사용자별 특정 폴더의 채팅 메시지 조회
    @GetMapping("/get-by-folder")
    public ResponseEntity getUserChatsByFolder(
        @RequestParam String userEmail,
        @RequestParam String folder) {
        return chatService.getUserChatsByFolder(userEmail, folder);
    }
    
    // 사용자별 폴더 목록 조회
    @GetMapping("/folders")
    public ResponseEntity getUserFolders(@RequestParam String userEmail) {
        return chatService.getUserFolders(userEmail);
    }
    
    // 테스트용 간단한 채팅 저장 (GET 요청)
    @GetMapping("/test-save")
    public ResponseEntity testSaveChat(
        @RequestParam String userEmail,
        @RequestParam String folder,
        @RequestParam String role,
        @RequestParam String text) {
        
        System.out.println("🧪 테스트용 채팅 저장 요청!");
        System.out.println("   - userEmail: " + userEmail);
        System.out.println("   - folder: " + folder);
        System.out.println("   - role: " + role);
        System.out.println("   - text: " + text);
        
        return chatService.saveChatMessage(userEmail, folder, role, text);
    }
    
    // 채팅 메시지 저장
    @PostMapping("/save")
    public ResponseEntity saveChatMessage(@RequestBody Map<String, String> request) {
        System.out.println("=" + "=".repeat(49));
        System.out.println("📨 ChatController: 채팅 저장 요청 수신!");
        System.out.println("=" + "=".repeat(49));
        System.out.println("📋 요청 전체 내용: " + request);
        
        String userEmail = request.get("userEmail");
        String folder = request.get("folder");
        String role = request.get("role");
        String text = request.get("text");
        
        System.out.println("🔍 파라미터 파싱 결과:");
        System.out.println("   - userEmail: " + userEmail);
        System.out.println("   - folder: " + folder);
        System.out.println("   - role: " + role);
        System.out.println("   - text: " + text);
        
        if (userEmail == null || folder == null || role == null || text == null) {
            System.err.println("❌ 필수 파라미터 누락!");
            System.err.println("   - userEmail null: " + (userEmail == null));
            System.err.println("   - folder null: " + (folder == null));
            System.err.println("   - role null: " + (role == null));
            System.err.println("   - text null: " + (text == null));
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "필수 파라미터가 누락되었습니다.");
            
            Map<String, Boolean> missing = new HashMap<>();
            missing.put("userEmail", userEmail == null);
            missing.put("folder", folder == null);
            missing.put("role", role == null);
            missing.put("text", text == null);
            errorResponse.put("missing", missing);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        System.out.println("✅ 파라미터 검증 통과! ChatService 호출...");
        System.out.println("=" + "=".repeat(49));
        
        return chatService.saveChatMessage(userEmail, folder, role, text);
    }
    
    // 사용자별 채팅 메시지 삭제
    @DeleteMapping("/delete")
    public ResponseEntity deleteUserChats(@RequestParam String userEmail) {
        return chatService.deleteUserChats(userEmail);
    }
    
    // 사용자별 특정 폴더의 채팅 메시지 삭제
    @DeleteMapping("/delete-by-folder")
    public ResponseEntity deleteUserChatsByFolder(
        @RequestParam String userEmail,
        @RequestParam String folder) {
        return chatService.deleteUserChatsByFolder(userEmail, folder);
    }
    
    // 사용자별 전체 채팅 메시지 삭제 (프론트 연동용)
    @DeleteMapping("/clear")
    public ResponseEntity clearUserChats(@RequestParam String userEmail) {
        return chatService.deleteUserChats(userEmail);
    }
}
