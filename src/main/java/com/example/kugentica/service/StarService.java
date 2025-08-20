package com.example.kugentica.service;

import com.example.kugentica.dto.BookmarkDetailResponse;
import com.example.kugentica.dto.SaveBookmarkRequest;
import com.example.kugentica.entity.Bookmark;
import com.example.kugentica.entity.CalendarEvent;
import com.example.kugentica.entity.Center; // Center 엔티티 import
import com.example.kugentica.entity.PolicyCode;
import com.example.kugentica.repository.BookmarkRepository;
import com.example.kugentica.repository.CalendarEventRepository;
import com.example.kugentica.repository.CenterRepository; // Center 레포지토리 import
import com.example.kugentica.repository.PolicyCodeRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarService {

    private final BookmarkRepository bookmarkRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final PolicyCodeRepository policyCodeRepository;
    private final CenterRepository centerRepository; // ⭐️ Center 조회를 위해 추가

    /**
     * 즐겨찾기를 저장합니다.
     */
    @Transactional
    public Bookmark saveStar(SaveBookmarkRequest request, String username) {
        // 이미 즐겨찾기했는지 확인
        if (bookmarkRepository.findByUserIdAndItemId(username, request.getItemId()).isPresent()) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 항목입니다.");
        }

        // ⭐️ Bookmark 엔티티에는 id와 type만 저장
        Bookmark newBookmark = new Bookmark(username, request, false);
        Bookmark savedBookmark = bookmarkRepository.save(newBookmark);

        // 캘린더 이벤트 생성 로직 호출
        createCalendarEventFromBookmark(savedBookmark);
        return savedBookmark;
    }

    /**
     * 즐겨찾기를 삭제합니다.
     */
    @Transactional
    public void deleteStar(ObjectId itemId, String username) {
        if (bookmarkRepository.existsByUserIdAndItemId(username, itemId)) {
            bookmarkRepository.deleteByUserIdAndItemId(username, itemId);
            // 캘린더 이벤트도 itemId 기준으로 삭제
            calendarEventRepository.deleteByIdAndUserId(itemId,username ); // 이 메서드 이름도 itemId를 쓰도록 변경하는 것을 권장
        } else {
            throw new RuntimeException("삭제할 즐겨찾기를 찾을 수 없습니다.");
        }
    }

    /**
     * ⭐️ 사용자의 모든 즐겨찾기 목록을 '상세 정보'와 함께 반환합니다.
     */
    public List<BookmarkDetailResponse> getAllStarsForUser(String userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "isPinned")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId, sort);

        return bookmarks.stream().map(bookmark -> {
            Object detailItem = null;
            // ⭐️ itemType에 따라 분기하여 적절한 Repository에서 상세 정보를 조회
            if ("POLICY".equalsIgnoreCase(bookmark.getItemType())) {
                detailItem = policyCodeRepository.findById(bookmark.getItemId()).orElse(null);
            } else if ("CENTER".equalsIgnoreCase(bookmark.getItemType())) {
                detailItem = centerRepository.findById(bookmark.getItemId()).orElse(null); // Center는 id로 조회한다고 가정
            }
            // 다른 타입이 추가되면 여기에 else if 추가

            return new BookmarkDetailResponse(bookmark, detailItem);
        }).collect(Collectors.toList());
    }

    /**
     * 즐겨찾기의 핀 상태를 토글합니다.
     */
    @Transactional
    public Bookmark togglePinStatus(ObjectId itemId, String username) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndItemId(username, itemId)
                .orElseThrow(() -> new RuntimeException("해당 즐겨찾기를 찾을 수 없습니다."));

        bookmark.setPinned(!bookmark.isPinned());
        bookmark.setUpdatedAt(LocalDateTime.now());
        return bookmarkRepository.save(bookmark);
    }

    /**
     * 즐겨찾기로부터 캘린더 이벤트를 생성합니다. (정책 타입만 해당)
     */
    private void createCalendarEventFromBookmark(Bookmark bookmark) {
        // ⭐️ 정책(POLICY) 타입의 즐겨찾기만 캘린더에 추가
        if (!"POLICY".equalsIgnoreCase(bookmark.getItemType())) {
            return;
        }

        // 정책 상세 정보를 DB에서 조회하여 마감일 확인
        policyCodeRepository.findById(bookmark.getItemId()).ifPresent(policyDetails -> {
            String deadline = policyDetails.getAplyYmd();
            if (deadline != null && !deadline.isBlank() && deadline.contains(" ~ ")) {
                try {
                    String endDateStr = deadline.split(" ~ ")[1];
                    LocalDate eventDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

                    CalendarEvent event = new CalendarEvent();
                    event.setUserId(bookmark.getUserId());
                    event.setPolicyId(bookmark.getItemId()); // policyId 대신 itemId를 저장
                    event.setTitle(policyDetails.getPlcyTitle());
                    event.setCategory(policyDetails.getPlcyKywdNm());
                    event.setEventDate(eventDate);

                    calendarEventRepository.save(event);
                } catch (Exception e) {
                    System.err.println("캘린더 이벤트 생성 실패: " + e.getMessage());
                }
            }
        });
    }

    // (getCalendarEventsByMonthForUser 메서드는 변경 없음)
    public List<CalendarEvent> getCalendarEventsByMonthForUser(int year, int month, String username) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return calendarEventRepository.findByEventDateBetweenAndUserId( startDate, endDate,username);
    }
}