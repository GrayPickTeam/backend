package com.graydang.app.domain.politicaltype.controller;

import com.graydang.app.domain.politicaltype.dto.request.QuestionSyncRequest;
import com.graydang.app.domain.politicaltype.dto.request.QuestionUpdateRequest;
import com.graydang.app.domain.politicaltype.dto.response.QuestionAdminResponse;
import com.graydang.app.domain.politicaltype.service.PoliticalTypeQuestionAdminService;
import com.graydang.app.global.common.model.dto.BaseResponse;
import com.graydang.app.global.common.model.enums.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/political-type/questions")
@RequiredArgsConstructor
@Tag(name = "Admin-PoliticalType-Question", description = "정치 유형 검사 문항 관리 API")
public class PoliticalTypeQuestionAdminController {

    private final PoliticalTypeQuestionAdminService questionAdminService;

    /** 전체 문항 조회 (displayOrder 순). */
    @Operation(summary = "문항 전체 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<List<QuestionAdminResponse>>> getAllQuestions() {
        List<QuestionAdminResponse> response = questionAdminService.getAllQuestions();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 문항 단건 조회. */
    @Operation(summary = "문항 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<QuestionAdminResponse>> getQuestion(@PathVariable Long id) {
        QuestionAdminResponse response = questionAdminService.getQuestion(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 문항 단건 수정 (내용만, 순서는 sync로). */
    @Operation(summary = "문항 단건 수정")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<QuestionAdminResponse>> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateRequest request) {

        QuestionAdminResponse response = questionAdminService.updateQuestion(id, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 문항 단건 삭제 + 나머지 displayOrder 재정렬. */
    @Operation(summary = "문항 단건 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteQuestion(@PathVariable Long id) {
        questionAdminService.deleteQuestion(id);
        return ResponseEntity.ok(BaseResponse.success(BaseResponseStatus.SUCCESS));
    }

    /**
     * 문항 벌크 동기화 (추가/수정/삭제/순서변경 한 번에).
     * 배열 순서가 곧 displayOrder.
     * id=null → 신규, id 있음 → 수정, 기존 DB에만 있고 목록에 없음 → 삭제.
     */
    @Operation(summary = "문항 벌크 동기화")
    @PutMapping("/sync")
    public ResponseEntity<BaseResponse<List<QuestionAdminResponse>>> syncQuestions(
            @Valid @RequestBody QuestionSyncRequest request) {

        List<QuestionAdminResponse> response = questionAdminService.syncQuestions(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
