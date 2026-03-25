package com.graydang.app.global.common.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringListConverterTest {

    private final StringListConverter converter = new StringListConverter();

    @Nested
    @DisplayName("convertToDatabaseColumn으로 List를 JSON으로 변환한다")
    class ConvertToDatabaseColumnTest {

        @Test
        @DisplayName("정상 리스트를 JSON 배열로 변환한다")
        void convertToDatabaseColumn_shouldConvertListToJson() {
            List<String> input = List.of("자유", "급진", "적극참여");

            String result = converter.convertToDatabaseColumn(input);

            assertThat(result).isEqualTo("[\"자유\",\"급진\",\"적극참여\"]");
            System.out.println("✅ convertToDatabaseColumn 테스트 통과 - " + result);
        }

        @Test
        @DisplayName("null이면 빈 배열 문자열을 반환한다")
        void convertToDatabaseColumn_shouldReturnEmptyArrayForNull() {
            String result = converter.convertToDatabaseColumn(null);

            assertThat(result).isEqualTo("[]");
            System.out.println("✅ null → [] 변환 테스트 통과");
        }
    }

    @Nested
    @DisplayName("convertToEntityAttribute로 JSON을 List로 변환한다")
    class ConvertToEntityAttributeTest {

        @Test
        @DisplayName("JSON 배열을 List로 변환한다")
        void convertToEntityAttribute_shouldConvertJsonToList() {
            String json = "[\"자유\",\"급진\"]";

            List<String> result = converter.convertToEntityAttribute(json);

            assertThat(result).containsExactly("자유", "급진");
            System.out.println("✅ convertToEntityAttribute 테스트 통과 - " + result.size() + "건");
        }

        @Test
        @DisplayName("null이면 빈 리스트를 반환한다")
        void convertToEntityAttribute_shouldReturnEmptyListForNull() {
            List<String> result = converter.convertToEntityAttribute(null);

            assertThat(result).isEqualTo(Collections.emptyList());
            System.out.println("✅ null → 빈 리스트 변환 테스트 통과");
        }
    }
}
