package com.graydang.app.global.common.model.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK,1000, "요청에 성공하였습니다."),

    /**
     * 2000 : RequestParam exception
     */
    METHOD_ARGUMENT_TYPE_MISMATCH(false, HttpStatus.BAD_REQUEST, 2001, "요청 파라미터 타입이 올바르지 않습니다."),
    HTTP_METHOD_TYPE_MISMATCH(false, HttpStatus.METHOD_NOT_ALLOWED, 2002, "지원되지 않는 Http Method입니다."),


    /**
     * 2500 : Bill exception
     */
    NONE_BILL(false, HttpStatus.BAD_REQUEST, 2501, "존재하지 않는 의안입니다."),

    /**
     * 2600 : User exception
     */
    NONE_USER(false, HttpStatus.BAD_REQUEST, 2601, "존재하지 않는 사용자입니다."),
    INVALID_USER_KEYWORD(false, HttpStatus.BAD_REQUEST, 2602, "유효하지 않은 사용자 키워드입니다."),

    /**
     * 2700 : File exception
     */
    INVALID_FILE_TYPE(false, HttpStatus.BAD_REQUEST, 2701, "지원하지 않는 파일 확장자입니다."),
    MALFORMED_UPLOAD_FILE(false, HttpStatus.BAD_REQUEST, 2702, "손상된 upload 파일입니다."),

    /**
     * 2800 : S3 exception
     */
    S3_UPLOAD_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR, 2801, "S3 파일 업로드에 실패했습니다."),
    S3_URL_GENERATION_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR, 2802, "S3 파일 URL 생성에 실패했습니다."),
    S3_FILE_DELETE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR, 2803, "S3 파일 삭제에 실패했습니다."),
    INVALID_S3_URI(false, HttpStatus.BAD_REQUEST, 2804, "S3 파일 정보를 확인할 수 없습니다."),

    /**
     * 2900 : JWT & 인증 관련 exception
     */
    INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED, 2901, "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_TOKEN(false, HttpStatus.UNAUTHORIZED, 2902, "만료된 JWT 토큰입니다."),
    BLACKLISTED_REFRESH_TOKEN(false, HttpStatus.UNAUTHORIZED, 2903, "사용이 중지된 리프레시 토큰입니다."),
    NOT_REFRESH_TOKEN(false, HttpStatus.BAD_REQUEST, 2904, "리프레시 토큰이 아닙니다."),
    INVALID_ACCESS_TOKEN(false, HttpStatus.UNAUTHORIZED, 2108, "잘못된 access token 입니다."),
    INVALID_TOKEN_TYPE(false, HttpStatus.UNAUTHORIZED, 2107, "잘못된 토큰 타입입니다."),

    // 권한 부족
    ACCESS_DENIED(false, HttpStatus.FORBIDDEN, 2951, "권한이 없는 유저의 접근입니다."),

    // 인증 실패
    UNAUTHORIZED_ACCESS(false, HttpStatus.UNAUTHORIZED, 2053, "로그인이 필요합니다."),

    /**
     * 3000: User Profile Exception
     */
    NONE_USER_PROFILE(false, HttpStatus.BAD_REQUEST, 3001, "사용자의 프로필이 존재하지 않습니다."),
    ALREADY_ONBOARDED(false, HttpStatus.BAD_REQUEST, 3002, "이미 온보딩이 완료된 사용자입니다."),
    ALREADY_WITHDRAWN_USER(false, HttpStatus.BAD_REQUEST, 3003, "이미 탈퇴한 사용자입니다."),
    ALREADY_BLOCKED_USER(false, HttpStatus.BAD_REQUEST, 3004, "이미 차단된 사용자입니다."),
    NOT_BLOCKED_USER(false, HttpStatus.BAD_REQUEST, 3005, "차단되지 않은 사용자입니다."),
    CANNOT_BLOCK_INACTIVE_USER(false, HttpStatus.BAD_REQUEST, 3006, "탈퇴한 사용자는 차단할 수 없습니다."),

    /**
     * 3100: Bill Reaction Exception
     */
    INVALID_REACTION_TYPE(false, HttpStatus.BAD_REQUEST, 3101, "존재하지 않는 반응 타입입니다."),

    /**
     * 3200: Comment Exception
     */
    NON_COMMENT(false, HttpStatus.BAD_REQUEST, 3201, "존재하지 않는 댓글입니다."),
    UNAUTHORIZED_COMMENT_ACCESS(false, HttpStatus.FORBIDDEN, 3202, "해당 댓글에 대한 권한이 없습니다."),

    /**
     * 3300: Comment Report Exception
     */
    ALREADY_REPORTED(false, HttpStatus.BAD_REQUEST, 3301, "이미 해당 댓글을 신고했습니다."),

    /**
     * 3400: Search Keyword Exception
     */
    SEARCH_KEYWORD_NOT_FOUND(false, HttpStatus.NOT_FOUND, 3401, "존재하지 않는 검색 키워드입니다."),
    SEARCH_KEYWORD_ALREADY_EXISTS(false, HttpStatus.BAD_REQUEST, 3402, "이미 존재하는 검색 키워드입니다."),

    /**
     * 3500: Political Type Exception
     */
    POLITICAL_TYPE_QUESTION_NOT_FOUND(false, HttpStatus.NOT_FOUND, 3501, "존재하지 않는 검사 문항입니다."),
    POLITICAL_TYPE_ANIMAL_NOT_FOUND(false, HttpStatus.NOT_FOUND, 3502, "존재하지 않는 동물 유형입니다."),
    POLITICAL_TYPE_RESULT_NOT_FOUND(false, HttpStatus.NOT_FOUND, 3503, "검사 결과가 존재하지 않습니다."),
    POLITICAL_TYPE_INVALID_ANSWER_COUNT(false, HttpStatus.BAD_REQUEST, 3504, "답변 수가 문항 수와 일치하지 않습니다."),
    POLITICAL_TYPE_ANIMAL_MAPPING_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR, 3505, "동물유형 매핑에 실패했습니다.");

    private final boolean isSuccess;
    @JsonIgnore
    private final HttpStatus httpStatus;
    private final int responseCode;
    private final String responseMessage;
}
