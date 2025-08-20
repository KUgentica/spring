package com.example.kugentica.service;

import com.example.kugentica.entity.Center;
import com.example.kugentica.entity.PolicyCode;
import com.example.kugentica.repository.CenterRepository;
import com.example.kugentica.repository.PolicyCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class PolicyService {
    @Autowired
    private PolicyCodeRepository policyCodeRepository;
    @Autowired
    private CenterRepository centerRepository;
    
    // 카테고리(키워드) 기반 조회 (제목/키워드 OR)
    public List<PolicyCode> getPoliciesByKeyword(String keyword) {
        final String q = keyword == null ? "" : keyword.trim();
        List<PolicyCode> raw = policyCodeRepository.searchByTitleOrKeyword(q);
        List<PolicyCode> deduped = raw.stream()
                .collect(Collectors.toMap(PolicyCode::getPlcyId, p -> p, (a, b) -> a))
                .values()
                .stream()
                .collect(Collectors.toList());
        int limit = Math.min(500, deduped.size());
        return deduped.subList(0, limit);
    }
    
    // 검색 기능을 위한 메서드들
    public List<PolicyCode> searchPolicies(String query) {
        System.out.println("=== [LOG] 정책 검색 요청: " + query);
        
        // 제목/키워드에서만 OR 검색으로 축소
        List<PolicyCode> raw = policyCodeRepository.searchByTitleOrKeyword(query);
        
        // ID 기준 중복 제거
        List<PolicyCode> deduped = raw.stream()
                .collect(Collectors.toMap(PolicyCode::getPlcyId, p -> p, (a, b) -> a))
                .values()
                .stream()
                .collect(Collectors.toList());
        
        // 최대 1000개로 제한 (필요시 조정)
        int limit = Math.min(1000, deduped.size());
        List<PolicyCode> result = deduped.subList(0, limit);
        
        System.out.println("=== [LOG] 정책 결과: raw=" + raw.size() + ", deduped=" + deduped.size() + ", returned=" + result.size());
        for (int i = 0; i < Math.min(3, result.size()); i++) {
            PolicyCode p = result.get(i);
            System.out.println("   -> plcyId=" + p.getPlcyId() + ", title=" + p.getPlcyTitle());
        }
        return result;
    }
    
    public List<Center> searchCenters(String query) {
        System.out.println("=== [LOG] 센터 검색 요청: " + query);
        
        List<Center> nameResults = centerRepository.findByCntrNmRegex(query);
        List<Center> addressResults = centerRepository.findByCntrAddrRegex(query);
        List<Center> detailAddrResults = centerRepository.findByCntrDaddrRegex(query);
        
        // 센터는 고유키(sn) 기준으로 중복 제거
        List<Center> deduped = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Center c : nameResults) {
            if (seen.add(c.getCntrSn())) deduped.add(c);
        }
        for (Center c : addressResults) {
            if (seen.add(c.getCntrSn())) deduped.add(c);
        }
        for (Center c : detailAddrResults) {
            if (seen.add(c.getCntrSn())) deduped.add(c);
        }
        
        // 최대 200개 제한
        int limit = Math.min(200, deduped.size());
        List<Center> result = deduped.subList(0, limit);
        System.out.println("=== [LOG] 센터 결과: name+addr+daddr=" + (nameResults.size()+addressResults.size()+detailAddrResults.size()) + ", deduped=" + deduped.size() + ", returned=" + result.size());
        return result;
    }
    
    public List<PolicyCode> getRecommendedPolicies() {
        System.out.println("=== [LOG] 추천 정책 요청");
        List<PolicyCode> allPolicies = policyCodeRepository.findAll();
        Collections.shuffle(allPolicies);
        int maxCount = Math.min(6, allPolicies.size());
        return allPolicies.subList(0, maxCount);
    }
    
    public List<Center> getCenters() { return centerRepository.findAll(); }
    public List<PolicyCode> getAllPolicies() { return policyCodeRepository.findAll(); }
}
