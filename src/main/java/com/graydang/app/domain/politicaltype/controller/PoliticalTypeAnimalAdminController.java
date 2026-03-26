package com.graydang.app.domain.politicaltype.controller;

import com.graydang.app.domain.politicaltype.dto.request.AnimalCreateRequest;
import com.graydang.app.domain.politicaltype.dto.request.AnimalUpdateRequest;
import com.graydang.app.domain.politicaltype.dto.response.AnimalAdminResponse;
import com.graydang.app.domain.politicaltype.service.PoliticalTypeAnimalAdminService;
import com.graydang.app.global.common.model.dto.BaseResponse;
import com.graydang.app.global.common.model.enums.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/political-type/animals")
@RequiredArgsConstructor
@Tag(name = "Admin-PoliticalType-Animal", description = "정치 유형 동물유형 콘텐츠 관리 API")
public class PoliticalTypeAnimalAdminController {

    private final PoliticalTypeAnimalAdminService animalAdminService;

    /** 동물유형 등록 (이미지 포함 Multipart). */
    @Operation(summary = "동물유형 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<AnimalAdminResponse>> createAnimal(
            @RequestPart("request") @Valid AnimalCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        AnimalAdminResponse response = animalAdminService.createAnimal(request, image);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 동물유형 전체 조회. */
    @Operation(summary = "동물유형 전체 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<List<AnimalAdminResponse>>> getAllAnimals() {
        List<AnimalAdminResponse> response = animalAdminService.getAllAnimals();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 동물유형 단건 조회. */
    @Operation(summary = "동물유형 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AnimalAdminResponse>> getAnimal(@PathVariable Long id) {
        AnimalAdminResponse response = animalAdminService.getAnimal(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 동물유형 콘텐츠 수정 (이미지 제외). */
    @Operation(summary = "동물유형 수정 (이미지 제외)")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AnimalAdminResponse>> updateAnimal(
            @PathVariable Long id,
            @Valid @RequestBody AnimalUpdateRequest request) {

        AnimalAdminResponse response = animalAdminService.updateAnimal(id, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 동물유형 이미지 교체. 기존 이미지 삭제 후 새 이미지 업로드. */
    @Operation(summary = "동물유형 이미지 수정")
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<AnimalAdminResponse>> updateAnimalImage(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image) {

        AnimalAdminResponse response = animalAdminService.updateAnimalImage(id, image);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /** 동물유형 삭제. S3 이미지도 함께 삭제. */
    @Operation(summary = "동물유형 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAnimal(@PathVariable Long id) {
        animalAdminService.deleteAnimal(id);
        return ResponseEntity.ok(BaseResponse.success(BaseResponseStatus.SUCCESS));
    }
}
