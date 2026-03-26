package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.dto.request.AnimalCreateRequest;
import com.graydang.app.domain.politicaltype.dto.request.AnimalUpdateRequest;
import com.graydang.app.domain.politicaltype.dto.response.AnimalAdminResponse;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeAnimalRepository;
import com.graydang.app.global.common.exception.BusinessException;
import com.graydang.app.global.common.model.enums.BaseResponseStatus;
import com.graydang.app.global.s3.model.ImagePrefix;
import com.graydang.app.global.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PoliticalTypeAnimalAdminService {

    private final PoliticalTypeAnimalRepository animalRepository;
    private final ImageService imageService;

    /**
     * 동물유형 콘텐츠 등록.
     * 이미지가 포함된 경우 S3 업로드 후 URL 저장. 실패 시 보상 삭제.
     */
    @Transactional
    public AnimalAdminResponse createAnimal(AnimalCreateRequest request, MultipartFile image) {
        PoliticalTypeAnimal compatible = resolveAnimal(request.getCompatibleAnimalId());
        PoliticalTypeAnimal incompatible = resolveAnimal(request.getIncompatibleAnimalId());

        String imageUrl = null;
        String originalName = null;

        // S3 업로드 → DB 저장. DB 실패 시 S3 보상 삭제
        if (image != null && !image.isEmpty()) {
            imageUrl = imageService.upload(image, ImagePrefix.POLITICAL_TYPE_ANIMAL);
            originalName = image.getOriginalFilename();
        }

        try {
            PoliticalTypeAnimal animal = PoliticalTypeAnimal.builder()
                    .code(request.getCode())
                    .name(request.getName())
                    .subtitle(request.getSubtitle())
                    .oneLiner(request.getOneLiner())
                    .description(request.getDescription())
                    .keywords(request.getKeywords())
                    .representativeFigures(request.getRepresentativeFigures())
                    .imageUrl(imageUrl)
                    .originalImageName(originalName)
                    .changePreferenceLevel(request.getChangePreferenceLevel())
                    .valueOrientationLevel(request.getValueOrientationLevel())
                    .compatibleAnimal(compatible)
                    .incompatibleAnimal(incompatible)
                    .build();

            return AnimalAdminResponse.from(animalRepository.save(animal));
        } catch (Exception e) {
            if (imageUrl != null) {
                log.warn("DB 저장 실패로 S3 이미지 보상 삭제. url={}", imageUrl);
                imageService.delete(imageUrl);
            }
            throw e;
        }
    }

    /** 전체 동물유형 조회. */
    public List<AnimalAdminResponse> getAllAnimals() {
        return animalRepository.findAll().stream()
                .map(AnimalAdminResponse::from)
                .toList();
    }

    /** 동물유형 단건 조회. */
    public AnimalAdminResponse getAnimal(Long id) {
        PoliticalTypeAnimal animal = findAnimalOrThrow(id);
        return AnimalAdminResponse.from(animal);
    }

    /** 동물유형 콘텐츠 수정 (이미지 제외). */
    @Transactional
    public AnimalAdminResponse updateAnimal(Long id, AnimalUpdateRequest request) {
        PoliticalTypeAnimal animal = findAnimalOrThrow(id);
        PoliticalTypeAnimal compatible = resolveAnimal(request.getCompatibleAnimalId());
        PoliticalTypeAnimal incompatible = resolveAnimal(request.getIncompatibleAnimalId());

        animal.update(
                request.getCode(),
                request.getName(),
                request.getSubtitle(),
                request.getOneLiner(),
                request.getDescription(),
                request.getKeywords(),
                request.getRepresentativeFigures(),
                request.getChangePreferenceLevel(),
                request.getValueOrientationLevel()
        );
        animal.updateCompatibility(compatible, incompatible);

        return AnimalAdminResponse.from(animal);
    }

    /**
     * 동물유형 이미지 교체.
     * 기존 이미지가 있으면 S3에서 삭제 후 새 이미지 업로드.
     */
    @Transactional
    public AnimalAdminResponse updateAnimalImage(Long id, MultipartFile image) {
        PoliticalTypeAnimal animal = findAnimalOrThrow(id);

        // 기존 이미지 S3 삭제
        if (animal.getImageUrl() != null) {
            imageService.delete(animal.getImageUrl());
        }

        // 새 이미지 업로드
        String newImageUrl = imageService.upload(image, ImagePrefix.POLITICAL_TYPE_ANIMAL);
        animal.updateImage(newImageUrl, image.getOriginalFilename());

        return AnimalAdminResponse.from(animal);
    }

    /** 동물유형 삭제. S3 이미지도 함께 삭제. */
    @Transactional
    public void deleteAnimal(Long id) {
        PoliticalTypeAnimal animal = findAnimalOrThrow(id);

        // S3 이미지 삭제
        if (animal.getImageUrl() != null) {
            imageService.delete(animal.getImageUrl());
        }

        animalRepository.deleteById(id);
    }

    private PoliticalTypeAnimal findAnimalOrThrow(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BaseResponseStatus.POLITICAL_TYPE_ANIMAL_NOT_FOUND));
    }

    private PoliticalTypeAnimal resolveAnimal(Long animalId) {
        if (animalId == null) {
            return null;
        }
        return findAnimalOrThrow(animalId);
    }
}
