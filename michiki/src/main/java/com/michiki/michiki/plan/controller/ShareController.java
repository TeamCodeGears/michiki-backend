package com.michiki.michiki.plan.controller;

import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/share")


public class ShareController {

    private final PlanRepository planRepository;

    @GetMapping("//{token}")
    public ResponseEntity<PlanResponseDto> getSharedPlan(@PathVariable String token) {
        Plan plan = planRepository.findByShareURI(token)
                .orElseThrow(()-> new RuntimeException("유효하지 않은 공유 URI입니다."));

        if(plan.getShareUriExpiresAt() == null || plan.getShareUriExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("공유 URI가 만료되었습니다.");
        }
        return ResponseEntity.ok(PlanResponseDto.fromEntity(plan));
    }
}