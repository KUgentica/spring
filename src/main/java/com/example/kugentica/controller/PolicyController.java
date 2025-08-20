package com.example.kugentica.controller;

import com.example.kugentica.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policy")
public class PolicyController {
    @Autowired
    private PolicyService policyService;
    
    // 카테고리(키워드) 조회 - 기존 클라이언트 호환
    @GetMapping("/category")
    public java.util.List<com.example.kugentica.entity.PolicyCode> getPoliciesByCategory(@RequestParam String keyword) {
        long t0 = System.currentTimeMillis();
        System.out.println("[API] GET /policy/category keyword=" + keyword);
        var result = policyService.getPoliciesByKeyword(keyword);
        long t1 = System.currentTimeMillis();
        System.out.println("[API] /policy/category size=" + result.size() + " elapsedMs=" + (t1 - t0));
        return result;
    }
    
    // 센터 전체 조회 - 기존 클라이언트 호환
    @GetMapping("/center")
    public java.util.List<com.example.kugentica.entity.Center> getCentersLegacy() {
        long t0 = System.currentTimeMillis();
        System.out.println("[API] GET /policy/center");
        var result = policyService.getCenters();
        long t1 = System.currentTimeMillis();
        System.out.println("[API] /policy/center size=" + result.size() + " elapsedMs=" + (t1 - t0));
        return result;
    }
    
    // 검색 기능
    @GetMapping("/search")
    public java.util.List<com.example.kugentica.entity.PolicyCode> searchPolicies(@RequestParam String query) {
        long t0 = System.currentTimeMillis();
        System.out.println("[API] GET /policy/search query=" + query);
        var result = policyService.searchPolicies(query);
        long t1 = System.currentTimeMillis();
        System.out.println("[API] /policy/search size=" + result.size() + " elapsedMs=" + (t1 - t0));
        return result;
    }
    
    @GetMapping("/search/centers")
    public java.util.List<com.example.kugentica.entity.Center> searchCenters(@RequestParam String query) {
        long t0 = System.currentTimeMillis();
        System.out.println("[API] GET /policy/search/centers query=" + query);
        var result = policyService.searchCenters(query);
        long t1 = System.currentTimeMillis();
        System.out.println("[API] /policy/search/centers size=" + result.size() + " elapsedMs=" + (t1 - t0));
        return result;
    }
    
    @GetMapping("/recommended")
    public java.util.List<com.example.kugentica.entity.PolicyCode> getRecommendedPolicies() {
        return policyService.getRecommendedPolicies();
    }
    
    // 신규 (선택 사항)
    @GetMapping("/centers")
    public java.util.List<com.example.kugentica.entity.Center> getCenters() {
        long t0 = System.currentTimeMillis();
        System.out.println("[API] GET /policy/centers");
        var result = policyService.getCenters();
        long t1 = System.currentTimeMillis();
        System.out.println("[API] /policy/centers size=" + result.size() + " elapsedMs=" + (t1 - t0));
        return result;
    }
    
    @GetMapping("/all")
    public java.util.List<com.example.kugentica.entity.PolicyCode> getAllPolicies() {
        long t0 = System.currentTimeMillis();
        System.out.println("[API] GET /policy/all");
        var result = policyService.getAllPolicies();
        long t1 = System.currentTimeMillis();
        System.out.println("[API] /policy/all size=" + result.size() + " elapsedMs=" + (t1 - t0));
        return result;
    }
}
