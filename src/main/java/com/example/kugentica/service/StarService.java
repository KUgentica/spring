package com.example.kugentica.service;

import com.example.kugentica.dto.BookmarkDetailResponse;
import com.example.kugentica.dto.SaveBookmarkRequest;
import com.example.kugentica.entity.Bookmark;
import com.example.kugentica.entity.CalendarEvent;
import com.example.kugentica.entity.Center;
import com.example.kugentica.entity.PolicyCode;
import com.example.kugentica.repository.BookmarkRepository;
import com.example.kugentica.repository.CalendarEventRepository;
import com.example.kugentica.repository.CenterRepository;
import com.example.kugentica.repository.PolicyCodeRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StarService {

  private final BookmarkRepository bookmarkRepository;
  private final CalendarEventRepository calendarEventRepository;
  private final PolicyCodeRepository policyCodeRepository;
  private final CenterRepository centerRepository;

  @Transactional
  public Bookmark saveStar(SaveBookmarkRequest request, String username) {
    if (bookmarkRepository.findByUserIdAndItemId(username, request.getItemId())
            .isPresent()) {
      throw new IllegalStateException("이미 즐겨찾기에 추가된 항목입니다.");
    }

    Bookmark newBookmark = new Bookmark(username, request, false);
    Bookmark savedBookmark = bookmarkRepository.save(newBookmark);

    createCalendarEventFromBookmark(savedBookmark);
    return savedBookmark;
  }

  @Transactional
  public void deleteStar(ObjectId itemId, String username) {
    System.out.println("--- 즐겨찾기 삭제 시작 ---");
    System.out.println("1. 삭제 대상 itemId: " + itemId);
    System.out.println("2. 사용자: " + username);

    if (bookmarkRepository.existsByUserIdAndItemId(username, itemId)) {
      System.out.println("3. 즐겨찾기 존재 확인됨");

      bookmarkRepository.deleteByUserIdAndItemId(username, itemId);
      System.out.println("4. 즐겨찾기 삭제 완료");

      System.out.println("5. 캘린더 이벤트 삭제 시작");
      System.out.println("   - 삭제할 사용자: " + username);
      System.out.println("   - 삭제할 정책 ID: " + itemId);

      try {
        calendarEventRepository.deleteByUserIdAndPolicyId(username, itemId);
        System.out.println("6. 캘린더 이벤트 삭제 완료");

        boolean stillExists =
            calendarEventRepository.existsByUserIdAndPolicyId(username, itemId);
        System.out.println("7. 삭제 후 캘린더 이벤트 존재 여부: " +
                           stillExists);

      } catch (Exception e) {
        System.err.println("캘린더 이벤트 삭제 중 오류 발생: " +
                           e.getMessage());
        e.printStackTrace();
      }

    } else {
      System.out.println("삭제할 즐겨찾기를 찾을 수 없음");
      throw new RuntimeException("삭제할 즐겨찾기를 찾을 수 없습니다.");
    }

    System.out.println("--- 즐겨찾기 삭제 종료 ---");
  }

  public List<BookmarkDetailResponse> getAllStarsForUser(String userId) {
    Sort sort = Sort.by(Sort.Direction.DESC, "isPinned")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));

    List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId, sort);

    return bookmarks.stream()
        .map(bookmark -> {
          Object detailItem = null;
          if ("POLICY".equalsIgnoreCase(bookmark.getItemType())) {
            detailItem = policyCodeRepository.findById(bookmark.getItemId())
                             .orElse(null);
          } else if ("CENTER".equalsIgnoreCase(bookmark.getItemType())) {
            detailItem =
                centerRepository.findById(bookmark.getItemId()).orElse(null);
          }

          return new BookmarkDetailResponse(bookmark, detailItem);
        })
        .collect(Collectors.toList());
  }

  public Bookmark togglePinStatus(ObjectId itemId, String username) {
    Bookmark bookmark =
        bookmarkRepository.findByUserIdAndItemId(username, itemId)
            .orElseThrow(()
                             -> new RuntimeException(
                                 "해당 즐겨찾기를 찾을 수 없습니다."));

    bookmark.setPinned(!bookmark.isPinned());
    bookmark.setUpdatedAt(LocalDateTime.now());
    return bookmarkRepository.save(bookmark);
  }

  private void createCalendarEventFromBookmark(Bookmark bookmark) {
    if (!"POLICY".equalsIgnoreCase(bookmark.getItemType())) {
      return;
    }

    policyCodeRepository.findById(bookmark.getItemId())
        .ifPresent(policyDetails -> {
          String deadline = policyDetails.getAplyYmd();
          if (deadline != null && !deadline.isBlank() &&
              deadline.contains(" ~ ")) {
            try {
              String endDateStr = deadline.split(" ~ ")[1];

              LocalDate eventDate = LocalDate.parse(
                  endDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

              LocalDate nowKST = LocalDate.now(ZoneOffset.of("+09:00"));

              if (eventDate.isAfter(nowKST.minusDays(1))) {
                CalendarEvent event = new CalendarEvent();
                event.setUserId(bookmark.getUserId());
                event.setPolicyId(bookmark.getItemId());
                event.setTitle(policyDetails.getPlcyTitle());
                event.setCategory(policyDetails.getPlcyKywdNm());
                event.setEventDate(eventDate);
                event.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

                calendarEventRepository.save(event);
                System.out.println("캘린더 이벤트 생성 성공: " + eventDate +
                                   " - " + event.getTitle());
              } else {
                System.out.println("마감된 정책은 캘린더에 추가하지 않음: " +
                                   eventDate);
              }
            } catch (Exception e) {
              System.err.println("캘린더 이벤트 생성 실패: " + e.getMessage() +
                                 " - 원본 데이터: " + deadline);
            }
          }
        });
  }

  public List<CalendarEvent>
  getCalendarEventsByMonthForUser(int year, int month, String username) {
    LocalDate startDate = LocalDate.of(year, month, 1);
    LocalDate nextMonthStart = startDate.plusMonths(1);

    System.out.println("캘린더 조회 기간(half-open): " + startDate + " ~ " +
                       nextMonthStart + " (사용자: " + username + ")");
    List<CalendarEvent> events =
        calendarEventRepository.findByEventDateBetweenAndUserId(
            startDate, nextMonthStart, username);

    List<CalendarEvent> filtered =
        events.stream()
            .filter(e
                    -> !e.getEventDate().isBefore(startDate) &&
                           e.getEventDate().isBefore(nextMonthStart))
            .collect(java.util.stream.Collectors.toList());

    System.out.println("조회된 이벤트 수(필터 후): " + filtered.size());
    return filtered;
  }
}