package com.example.kugentica.service;

import com.example.kugentica.entity.Chat;
import com.example.kugentica.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    
    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
    
    // 사용자별 채팅 메시지 조회
    public ResponseEntity getUserChats(String userEmail) {
        try {
            System.out.println("💬 사용자 채팅 조회 시도");
            System.out.println("📧 이메일: " + userEmail);
            
            List<Chat> chats = chatRepository.findByUserEmailOrderByTimestampAsc(userEmail);
            
            System.out.println("✅ 채팅 조회 완료! 총 " + chats.size() + "개 메시지");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", chats);
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("💥 채팅 조회 중 오류 발생!");
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("채팅 조회 중 오류가 발생했습니다.");
        }
    }
    
    // 사용자별 특정 폴더의 채팅 메시지 조회
    public ResponseEntity getUserChatsByFolder(String userEmail, String folder) {
        try {
            System.out.println("💬 사용자 폴더별 채팅 조회 시도");
            System.out.println("📧 이메일: " + userEmail);
            System.out.println("📁 폴더: " + folder);
            
            List<Chat> chats = chatRepository.findByUserEmailAndFolderOrderByTimestampAsc(userEmail, folder);
            
            System.out.println("✅ 폴더별 채팅 조회 완료! 총 " + chats.size() + "개 메시지");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", chats);
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("💥 폴더별 채팅 조회 중 오류 발생!");
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("폴더별 채팅 조회 중 오류가 발생했습니다.");
        }
    }
    
    // 사용자별 폴더 목록 조회
    public ResponseEntity getUserFolders(String userEmail) {
        try {
            System.out.println("📁 사용자 폴더 목록 조회 시도");
            System.out.println("📧 이메일: " + userEmail);
            
            List<Chat> folderChats = chatRepository.findFoldersByUserEmail(userEmail);
            
            // 폴더명만 추출하여 중복 제거
            List<String> folders = folderChats.stream()
                .map(Chat::getFolder)
                .distinct()
                .collect(Collectors.toList());
            
            System.out.println("✅ 폴더 목록 조회 완료! 총 " + folders.size() + "개 폴더");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", folders);
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("💥 폴더 목록 조회 중 오류 발생!");
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("폴더 목록 조회 중 오류가 발생했습니다.");
        }
    }
    
    // 채팅 메시지 저장
    public ResponseEntity saveChatMessage(String userEmail, String folder, String role, String text) {
        try {
            System.out.println("=" + "=".repeat(49));
            System.out.println("💬 채팅 메시지 저장 시작!");
            System.out.println("=" + "=".repeat(49));
            System.out.println("📧 사용자 이메일: " + userEmail);
            System.out.println("📁 폴더: " + folder);
            System.out.println("👤 역할: " + role);
            System.out.println("📝 메시지 내용: " + text);
            System.out.println("⏰ 저장 시도 시간: " + java.time.LocalDateTime.now());
            
            // Chat 객체 생성
            Chat chat = Chat.builder()
                .userEmail(userEmail)
                .folder(folder)
                .role(role)
                .text(text)
                .build();
            
            System.out.println("🔨 Chat 객체 생성 완료:");
            System.out.println("   - userEmail: " + chat.getUserEmail());
            System.out.println("   - folder: " + chat.getFolder());
            System.out.println("   - role: " + chat.getRole());
            System.out.println("   - text: " + chat.getText());
            System.out.println("   - timestamp: " + chat.getTimestamp());
            
            // MongoDB에 저장
            System.out.println("💾 MongoDB 저장 시도 중...");
            Chat savedChat = chatRepository.save(chat);
            
            System.out.println("✅ MongoDB 저장 성공!");
            System.out.println("   - 저장된 Chat ID: " + savedChat.getChatId());
            System.out.println("   - 최종 userEmail: " + savedChat.getUserEmail());
            System.out.println("   - 최종 folder: " + savedChat.getFolder());
            System.out.println("   - 최종 role: " + savedChat.getRole());
            System.out.println("   - 최종 text: " + savedChat.getText());
            System.out.println("   - 최종 timestamp: " + savedChat.getTimestamp());
            
            // 저장 후 DB에서 다시 조회해서 확인
            System.out.println("🔍 저장 확인을 위해 DB에서 재조회...");
            List<Chat> verifyChats = chatRepository.findByUserEmailAndFolderOrderByTimestampAsc(userEmail, folder);
            System.out.println("   - DB에서 조회된 메시지 수: " + verifyChats.size());
            if (!verifyChats.isEmpty()) {
                Chat lastChat = verifyChats.get(verifyChats.size() - 1);
                System.out.println("   - 마지막 메시지 ID: " + lastChat.getChatId());
                System.out.println("   - 마지막 메시지 내용: " + lastChat.getText());
            }
            
            System.out.println("=" + "=".repeat(49));
            System.out.println("🎉 채팅 메시지 저장 완료!");
            System.out.println("=" + "=".repeat(49));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "채팅 메시지가 저장되었습니다.");
            response.put("chatId", savedChat.getChatId().toString());
            response.put("timestamp", savedChat.getTimestamp().toString());
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("=" + "=".repeat(49));
            System.err.println("💥 채팅 메시지 저장 중 오류 발생!");
            System.err.println("=" + "=".repeat(49));
            System.err.println("❌ 오류 타입: " + e.getClass().getSimpleName());
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            System.err.println("❌ 오류 발생 시간: " + java.time.LocalDateTime.now());
            System.err.println("❌ 입력받은 파라미터:");
            System.err.println("   - userEmail: " + userEmail);
            System.err.println("   - folder: " + folder);
            System.err.println("   - role: " + role);
            System.err.println("   - text: " + text);
            e.printStackTrace();
            System.err.println("=" + "=".repeat(49));
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "채팅 메시지 저장 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // 사용자별 채팅 메시지 삭제
    public ResponseEntity deleteUserChats(String userEmail) {
        try {
            System.out.println("🗑️ 사용자 채팅 삭제 시도");
            System.out.println("📧 이메일: " + userEmail);
            
            long deletedCount = chatRepository.countByUserEmail(userEmail);
            chatRepository.deleteByUserEmail(userEmail);
            
            System.out.println("✅ 채팅 삭제 완료! 삭제된 메시지: " + deletedCount + "개");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "채팅 메시지가 삭제되었습니다.");
            response.put("deletedCount", deletedCount);
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("💥 채팅 삭제 중 오류 발생!");
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("채팅 삭제 중 오류가 발생했습니다.");
        }
    }
    
    // 사용자별 특정 폴더의 채팅 메시지 삭제
    public ResponseEntity deleteUserChatsByFolder(String userEmail, String folder) {
        try {
            System.out.println("🗑️ 사용자 폴더별 채팅 삭제 시도");
            System.out.println("📧 이메일: " + userEmail);
            System.out.println("📁 폴더: " + folder);
            
            long deletedCount = chatRepository.countByUserEmailAndFolder(userEmail, folder);
            chatRepository.deleteByUserEmailAndFolder(userEmail, folder);
            
            System.out.println("✅ 폴더별 채팅 삭제 완료! 삭제된 메시지: " + deletedCount + "개");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "폴더별 채팅 메시지가 삭제되었습니다.");
            response.put("deletedCount", deletedCount);
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("💥 폴더별 채팅 삭제 중 오류 발생!");
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("폴더별 채팅 삭제 중 오류가 발생했습니다.");
        }
    }
}
