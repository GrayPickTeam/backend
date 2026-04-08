package com.graydang.app.domain.politicaltype.controller;

import com.graydang.app.domain.auth.oauth2.CustomUserDetails;
import com.graydang.app.domain.politicaltype.dto.request.TestSubmitRequest;
import com.graydang.app.domain.politicaltype.dto.response.*;
import com.graydang.app.domain.politicaltype.service.PoliticalTypeService;
import com.graydang.app.global.common.model.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 정치유형 검사 사용자 API. */
@RestController
@RequestMapping("/api/political-type")
@RequiredArgsConstructor
@Tag(name = "PoliticalType", description = "정치유형 검사 사용자 API")
public class PoliticalTypeController {

    private final PoliticalTypeService politicalTypeService;

    @Operation(summary = "검사 문항 조회")
    @GetMapping("/questions")
    public ResponseEntity<BaseResponse<List<QuestionResponse>>> getQuestions() {
        List<QuestionResponse> response = politicalTypeService.getQuestions();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 비회원도 제출 가능. 인증 토큰이 있으면 자동 저장. */
    @Operation(summary = "검사 답변 제출")
    @PostMapping("/submit")
    public ResponseEntity<BaseResponse<TestResultResponse>> submitTest(
            @Valid @RequestBody TestSubmitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        var user = userDetails != null ? userDetails.getUser() : null;
        TestResultResponse response = politicalTypeService.submitTest(request, user);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "비회원 결과 저장 (로그인 후)")
    @PostMapping("/save")
    public ResponseEntity<BaseResponse<TestResultResponse>> saveResult(
            @Valid @RequestBody TestSubmitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TestResultResponse response = politicalTypeService.saveResult(request, userDetails.getUser());
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "내 검사 결과 조회")
    @GetMapping("/result")
    public ResponseEntity<BaseResponse<TestResultResponse>> getMyResult(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TestResultResponse response = politicalTypeService.getMyResult(userDetails.getUser());
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "유형별 랭킹 조회")
    @GetMapping("/ranking")
    public ResponseEntity<BaseResponse<RankingResponse>> getRanking() {
        RankingResponse response = politicalTypeService.getRanking();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "동물유형 상세 조회")
    @GetMapping("/animals/{code}")
    public ResponseEntity<BaseResponse<AnimalDetailResponse>> getAnimalDetail(
            @PathVariable String code) {

        AnimalDetailResponse response = politicalTypeService.getAnimalDetail(code);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "전체 참여자 수 조회")
    @GetMapping("/participant-count")
    public ResponseEntity<BaseResponse<Map<String, Long>>> getParticipantCount() {
        long count = politicalTypeService.getParticipantCount();
        return ResponseEntity.ok(BaseResponse.success(Map.of("count", count)));
    }
}
