package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.dto.request.AnimalCreateRequest;
import com.graydang.app.domain.politicaltype.dto.response.AnimalAdminResponse;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeAnimalRepository;
import com.graydang.app.global.common.exception.BusinessException;
import com.graydang.app.global.s3.model.ImagePrefix;
import com.graydang.app.global.s3.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PoliticalTypeAnimalAdminServiceTest {

    @InjectMocks
    private PoliticalTypeAnimalAdminService animalAdminService;

    @Mock
    private PoliticalTypeAnimalRepository animalRepository;

    @Mock
    private ImageService imageService;

    private PoliticalTypeAnimal createDolphin() {
        return PoliticalTypeAnimal.builder()
                .code("DOLPHIN")
                .name("돌고래")
                .subtitle("정의로운 혁명가")
                .oneLiner("바꿔야 한다면, 지금이야!")
                .description("돌고래 유형 상세 설명")
                .keywords(List.of("자유", "급진"))
                .representativeFigures(List.of("인물A"))
                .imageUrl("https://s3.../dolphin.png")
                .originalImageName("dolphin_character.png")
                .changePreferenceLevel(ScoreLevel.HIGH)
                .valueOrientationLevel(ScoreLevel.HIGH)
                .build();
    }

    @Nested
    @DisplayName("createAnimal 동물유형 생성 시")
    class CreateAnimalTest {

        @Test
        @DisplayName("이미지와 함께 정상 생성한다")
        void createAnimal_withImage_shouldUploadAndCreate() {
            PoliticalTypeAnimal dolphin = createDolphin();
            given(animalRepository.save(any(PoliticalTypeAnimal.class))).willReturn(dolphin);
            given(imageService.upload(any(MultipartFile.class), eq(ImagePrefix.POLITICAL_TYPE_ANIMAL)))
                    .willReturn("https://s3.../dolphin.png");

            AnimalCreateRequest request = new AnimalCreateRequest();
            setField(request, "code", "DOLPHIN");
            setField(request, "name", "돌고래");
            setField(request, "oneLiner", "바꿔야 한다면, 지금이야!");
            setField(request, "description", "돌고래 유형 상세 설명");
            setField(request, "keywords", List.of("자유", "급진"));
            setField(request, "representativeFigures", List.of("인물A"));
            setField(request, "changePreferenceLevel", ScoreLevel.HIGH);
            setField(request, "valueOrientationLevel", ScoreLevel.HIGH);

            MockMultipartFile image = new MockMultipartFile(
                    "image", "dolphin_character.png", "image/png", "fake-image".getBytes());

            AnimalAdminResponse response = animalAdminService.createAnimal(request, image);

            assertThat(response.getCode()).isEqualTo("DOLPHIN");
            verify(imageService).upload(any(MultipartFile.class), eq(ImagePrefix.POLITICAL_TYPE_ANIMAL));
            verify(animalRepository).save(any(PoliticalTypeAnimal.class));

            System.out.println("✅ createAnimal (이미지 포함) 테스트 통과");
        }

        @Test
        @DisplayName("이미지 없이도 정상 생성한다")
        void createAnimal_withoutImage_shouldCreateWithNullImageUrl() {
            PoliticalTypeAnimal dolphin = PoliticalTypeAnimal.builder()
                    .code("DOLPHIN").name("돌고래").oneLiner("요약").description("설명")
                    .changePreferenceLevel(ScoreLevel.HIGH).valueOrientationLevel(ScoreLevel.HIGH)
                    .build();
            given(animalRepository.save(any(PoliticalTypeAnimal.class))).willReturn(dolphin);

            AnimalCreateRequest request = new AnimalCreateRequest();
            setField(request, "code", "DOLPHIN");
            setField(request, "name", "돌고래");
            setField(request, "oneLiner", "요약");
            setField(request, "description", "설명");
            setField(request, "changePreferenceLevel", ScoreLevel.HIGH);
            setField(request, "valueOrientationLevel", ScoreLevel.HIGH);

            AnimalAdminResponse response = animalAdminService.createAnimal(request, null);

            assertThat(response.getCode()).isEqualTo("DOLPHIN");
            verify(imageService, never()).upload(any(), any());

            System.out.println("✅ createAnimal (이미지 없음) 테스트 통과");
        }
    }

    @Nested
    @DisplayName("getAnimal 동물유형 단건 조회 시")
    class GetAnimalTest {

        @Test
        @DisplayName("존재하는 ID면 동물유형을 반환한다")
        void getAnimal_shouldReturnAnimal() {
            given(animalRepository.findById(1L)).willReturn(Optional.of(createDolphin()));

            AnimalAdminResponse response = animalAdminService.getAnimal(1L);

            assertThat(response.getCode()).isEqualTo("DOLPHIN");
            assertThat(response.getOriginalImageName()).isEqualTo("dolphin_character.png");
            System.out.println("✅ getAnimal 테스트 통과 - originalImageName 포함 확인");
        }

        @Test
        @DisplayName("존재하지 않는 ID면 BusinessException을 던진다")
        void getAnimal_shouldThrowWhenNotFound() {
            given(animalRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> animalAdminService.getAnimal(999L))
                    .isInstanceOf(BusinessException.class);

            System.out.println("✅ getAnimal_shouldThrowWhenNotFound 테스트 통과");
        }
    }

    @Nested
    @DisplayName("deleteAnimal 동물유형 삭제 시")
    class DeleteAnimalTest {

        @Test
        @DisplayName("이미지가 있으면 S3 삭제 후 Entity를 삭제한다")
        void deleteAnimal_withImage_shouldDeleteS3AndEntity() {
            PoliticalTypeAnimal dolphin = createDolphin();
            given(animalRepository.findById(1L)).willReturn(Optional.of(dolphin));

            animalAdminService.deleteAnimal(1L);

            verify(imageService).delete("https://s3.../dolphin.png");
            verify(animalRepository).deleteById(1L);
            System.out.println("✅ deleteAnimal 테스트 통과 - S3 이미지 삭제 + Entity 삭제");
        }

        @Test
        @DisplayName("존재하지 않는 ID면 BusinessException을 던진다")
        void deleteAnimal_shouldThrowWhenNotFound() {
            given(animalRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> animalAdminService.deleteAnimal(999L))
                    .isInstanceOf(BusinessException.class);

            System.out.println("✅ deleteAnimal_shouldThrowWhenNotFound 테스트 통과");
        }
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
