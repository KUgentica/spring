package com.example.kugentica.controller;

import com.example.kugentica.dto.BookmarkDetailResponse;
import com.example.kugentica.dto.CustomUserDetails;
import com.example.kugentica.dto.SaveBookmarkRequest;
import com.example.kugentica.entity.Bookmark;
import com.example.kugentica.entity.CalendarEvent;
import com.example.kugentica.service.StarService; // BookmarkService -> StarService로 변경
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/star") // ★ Flutter와 일치하도록 경로를 /star로 변경
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class StarController {

    private final StarService starService;

    /**
     * 즐겨찾기를 추가하거나, 이미 있다면 기존 것을 반환합니다. (토글 방식)
     * Flutter의 saveBookmark(policy) 요청을 처리합니다.
     */
    @PostMapping("/save")
    public ResponseEntity<Bookmark> saveStar(@RequestBody SaveBookmarkRequest request) {
        String username = getAuthenticatedUserEmail();
        Bookmark savedBookmark = starService.saveStar(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookmark);
    }

    /**
     * 즐겨찾기를 제거합니다.
     * Flutter의 removeBookmark(policyId) 요청을 처리합니다.
     */
    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<Void> deleteStar(@PathVariable ObjectId itemId) {
        String username = getAuthenticatedUserEmail();
        starService.deleteStar(itemId, username);
        return ResponseEntity.ok().build();
    }

    /**
     * 현재 로그인된 사용자의 모든 즐겨찾기 목록을 조회합니다.
     * Flutter의 getBookmarks() 요청을 처리합니다.
     */
    @GetMapping("/get")
    public ResponseEntity<List<BookmarkDetailResponse>> getAllStars() {
        String username = getAuthenticatedUserEmail();
        List<BookmarkDetailResponse> bookmarks = starService.getAllStarsForUser(username);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * 즐겨찾기의 핀 상태를 토글합니다.
     * Flutter의 togglePin(policyId) 요청을 처리합니다.
     */
    @PatchMapping("/pin/{policyId}")
    public ResponseEntity<Bookmark> togglePin(@PathVariable ObjectId policyId) {
        String username = getAuthenticatedUserEmail();
        Bookmark updatedBookmark = starService.togglePinStatus(policyId, username);
        return ResponseEntity.ok(updatedBookmark);
    }

    /**
     * 특정 년도와 월에 해당하는 사용자의 캘린더 이벤트를 조회합니다.
     */
    @GetMapping("/getCalendar/{year}/{month}")
    public ResponseEntity<List<CalendarEvent>> getCalendarEvents(
            @PathVariable int year,
            @PathVariable int month
    ) {
        String username = getAuthenticatedUserEmail();
        List<CalendarEvent> events = starService.getCalendarEventsByMonthForUser(year, month, username);
        return ResponseEntity.ok(events);
    }

    /**
     * SecurityContext에서 인증된 사용자 이름을 가져오는 헬퍼 메소드
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            throw new SecurityException("인증된 사용자가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // UserDetails를 구현한 사용자 정의 객체일 경우
            // UserDetails 인터페이스 자체에는 getUsername()만 있으므로, 실제 클래스로 캐스팅해야 합니다.
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getUsername(); // 사용자 정의 클래스에 getEmail() 메서드가 있다고 가정
        } else {
            // Principal이 문자열인 경우 (드문 경우)
            // 이 경우엔 principal 객체가 바로 username(String)일 수 있습니다.
            // 여기서는 이메일을 직접 얻을 수 없으므로, DB 조회가 필요할 수 있습니다.
            throw new IllegalStateException("Principal 객체가 예상된 타입이 아닙니다: " + principal.getClass());
        }
    }
}
