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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        Bookmark newBookmark = new Bookmark(username, request, false); // 즐겨찾기 상태로 저장
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
        System.out.println("--- 🗑️ 즐겨찾기 삭제 시작 ---");
        System.out.println("1. 삭제 대상 itemId: " + itemId);
        System.out.println("2. 사용자: " + username);
        
        if (bookmarkRepository.existsByUserIdAndItemId(username, itemId)) {
            System.out.println("3. 즐겨찾기 존재 확인됨");
            
            // 즐겨찾기 삭제
            bookmarkRepository.deleteByUserIdAndItemId(username, itemId);
            System.out.println("4. 즐겨찾기 삭제 완료");
            
            // 캘린더 이벤트도 itemId 기준으로 삭제
            System.out.println("5. 캘린더 이벤트 삭제 시작");
            System.out.println("   - 삭제할 사용자: " + username);
            System.out.println("   - 삭제할 정책 ID: " + itemId);
            
            try {
                calendarEventRepository.deleteByUserIdAndPolicyId(username, itemId);
                System.out.println("6. 캘린더 이벤트 삭제 완료");
                
                // 삭제 후 확인
                boolean stillExists = calendarEventRepository.existsByUserIdAndPolicyId(username, itemId);
                System.out.println("7. 삭제 후 캘린더 이벤트 존재 여부: " + stillExists);
                
            } catch (Exception e) {
                System.err.println("❌ 캘린더 이벤트 삭제 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
            
        } else {
            System.out.println("❌ 삭제할 즐겨찾기를 찾을 수 없음");
            throw new RuntimeException("삭제할 즐겨찾기를 찾을 수 없습니다.");
        }
        
        System.out.println("--- ✅ 즐겨찾기 삭제 종료 ---");
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
                    
                    // 한국 시간대 기준으로 날짜 파싱 (UTC+9)
                    LocalDate eventDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    
                    // 한국 시간대 기준으로 현재 시간 가져오기 (UTC+9)
                    LocalDate nowKST = LocalDate.now(ZoneOffset.of("+09:00"));
                    
                    // 한국 시간대 기준으로 날짜가 유효한지 확인 (어제까지는 허용)
                    if (eventDate.isAfter(nowKST.minusDays(1))) {
                        // 중복 체크 없이 바로 캘린더 이벤트 생성
                        CalendarEvent event = new CalendarEvent();
                        event.setUserId(bookmark.getUserId());
                        event.setPolicyId(bookmark.getItemId()); // policyId 대신 itemId를 저장
                        event.setTitle(policyDetails.getPlcyTitle());
                        event.setCategory(policyDetails.getPlcyKywdNm());
                        // 원래 날짜 그대로 저장
                        event.setEventDate(eventDate);
                        event.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC)); // UTC 기준 생성 시간 설정

                        calendarEventRepository.save(event);
                        System.out.println("캘린더 이벤트 생성 성공: " + eventDate + " - " + event.getTitle());
                    } else {
                        System.out.println("마감된 정책은 캘린더에 추가하지 않음: " + eventDate);
                    }
                } catch (Exception e) {
                    System.err.println("캘린더 이벤트 생성 실패: " + e.getMessage() + " - 원본 데이터: " + deadline);
                }
            }
        });
    }

    public List<CalendarEvent> getCalendarEventsByMonthForUser(int year, int month, String username) {
        // 한국 시간대 기준으로 월 시작일과 (다음달 1일 = 상한 배타) 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate nextMonthStart = startDate.plusMonths(1); // [start, next) 형태로 검색

        System.out.println("캘린더 조회 기간(half-open): " + startDate + " ~ " + nextMonthStart + " (사용자: " + username + ")");

        // 저장소 메서드가 Between(포함/포함)이라도 nextMonthStart를 상한으로 두고 이후에 배타 필터링한다
        List<CalendarEvent> events = calendarEventRepository.findByEventDateBetweenAndUserId(startDate, nextMonthStart, username);

        // 안전하게 [startDate, nextMonthStart) 윈도우만 남기고, 해당 month 데이터만 반환
        List<CalendarEvent> filtered = events.stream()
                .filter(e -> !e.getEventDate().isBefore(startDate) && e.getEventDate().isBefore(nextMonthStart))
                .collect(java.util.stream.Collectors.toList());

        System.out.println("조회된 이벤트 수(필터 후): " + filtered.size());
        return filtered;
    }
    

}