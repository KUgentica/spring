package com.example.kugentica.controller;

import com.example.kugentica.dto.BookmarkDetailResponse;
import com.example.kugentica.dto.CustomUserDetails;
import com.example.kugentica.dto.SaveBookmarkRequest;
import com.example.kugentica.entity.Bookmark;
import com.example.kugentica.entity.CalendarEvent;
import com.example.kugentica.service.StarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/star")
@RequiredArgsConstructor
public class StarController {

  private final StarService starService;

  /**
   * 즐겨찾기를 추가하거나, 이미 있다면 기존 것을 반환합니다. (토글 방식)
   * Flutter의 saveBookmark(policy) 요청을 처리합니다.
   */
  @PostMapping("/save")
  public ResponseEntity<Bookmark>
  saveStar(@RequestBody SaveBookmarkRequest request) {
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

  @GetMapping("/get")
  public ResponseEntity<List<BookmarkDetailResponse>> getAllStars() {
    String username = getAuthenticatedUserEmail();
    List<BookmarkDetailResponse> bookmarks =
        starService.getAllStarsForUser(username);
    return ResponseEntity.ok(bookmarks);
  }

  @PatchMapping("/pin/{policyId}")
  public ResponseEntity<Bookmark> togglePin(@PathVariable ObjectId policyId) {
    String username = getAuthenticatedUserEmail();
    Bookmark updatedBookmark = starService.togglePinStatus(policyId, username);
    return ResponseEntity.ok(updatedBookmark);
  }

  @GetMapping("/getCalendar/{year}/{month}")
  public ResponseEntity<List<CalendarEvent>>
  getCalendarEvents(@PathVariable int year, @PathVariable int month) {
    String username = getAuthenticatedUserEmail();
    List<CalendarEvent> events =
        starService.getCalendarEventsByMonthForUser(year, month, username);
    return ResponseEntity.ok(events);
  }

  private String getAuthenticatedUserEmail() {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() ||
        "anonymousUser".equals(authentication.getPrincipal().toString())) {
      throw new SecurityException("인증된 사용자가 없습니다.");
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails)principal;
      return userDetails.getUsername();

    } else {
      throw new IllegalStateException(
          "Principal 객체가 예상된 타입이 아닙니다: " + principal.getClass());
    }
  }
}
