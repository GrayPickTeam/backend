package com.graydang.app.global.common.handler;

import com.graydang.app.common.exception.ApiException;
import com.graydang.app.common.exception.ErrorLevel;
import com.graydang.app.common.exception.response.ExceptionResponse;
import com.graydang.app.global.common.model.dto.BaseResponse;
import com.graydang.app.monitoring.SlackNotifier;
import com.graydang.app.monitoring.SlackPayload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.graydang.app.global.common.model.enums.BaseResponseStatus.HTTP_METHOD_TYPE_MISMATCH;
import static com.graydang.app.global.common.model.enums.BaseResponseStatus.METHOD_ARGUMENT_TYPE_MISMATCH;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final SlackNotifier slack;

    private static final String SKIP_NOTIFICATION_PATH = "/";

    public GlobalExceptionHandler(@Qualifier("asyncSlackNotifier") SlackNotifier slack) {
        this.slack = slack;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException(ApiException e, HttpServletRequest request){
        // 1. 실제 발생 위치(= 사용자 코드 지점) 찾기
        StackTraceElement[] stackTrace = e.getStackTrace();
        StackTraceElement userStack = null;

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            // ApiException 자체나 common.exception 패키지가 아닌 곳을 잡아내자
            if (!className.startsWith("com.graydang.app.common.exception")) {
                userStack = element;
                break;
            }
        }

        // 2. userStack에서 location 정보를 만든다.
        //    예: "com.mycompany.service.PatientService.findPatient():123"
        String location = "Unknown";
        if (userStack != null) {
            location =
                String.format(
                    "%s.%s():%d",
                    userStack.getClassName(), userStack.getMethodName(), userStack.getLineNumber());
        }

        if (e.getLevel().equals(ErrorLevel.LV4) && !SKIP_NOTIFICATION_PATH.equals(request.getRequestURI())) {
            slack.send(new SlackPayload(
                "errorMsg: " + e.getMessage() + "\nlocation: " + location,
                "#monitoring", true));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse.of(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResponse<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {

        JSONObject result = new JSONObject();
        result.put(e.getName(), e.getMessage());
        return new BaseResponse<>(METHOD_ARGUMENT_TYPE_MISMATCH, result);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResponse<?> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {

        JSONObject result = new JSONObject();
        result.put(e.getMethod(), e.getMessage());
        return new BaseResponse<>(HTTP_METHOD_TYPE_MISMATCH, result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnexpectedException(Exception e, HttpServletRequest request) {
        String location = Arrays.stream(e.getStackTrace())
            .filter(el -> el.getClassName().startsWith("com.graydang.app"))
            .findFirst()
            .map(el -> String.format("%s.%s():%d", el.getClassName(), el.getMethodName(), el.getLineNumber()))
            .orElse("Unknown");

        String requestUUID = MDC.get("requestUUID");

        log.error("[500 ERROR] requestUUID={} message={} location={}", requestUUID, e.getMessage(), location, e);

        if (!SKIP_NOTIFICATION_PATH.equals(request.getRequestURI())) {
            slack.send(new SlackPayload(
                String.format("[500 ERROR]\nrequestUUID: %s\nuri: %s\nmessage: %s\nlocation: %s",
                    requestUUID, request.getRequestURI(), e.getMessage(), location),
                "#monitoring", null));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse.of("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        Map<String, Object> errorMap = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(METHOD_ARGUMENT_TYPE_MISMATCH, errorMap));

    }
}
