package com.project.admin_system.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("[BusinessException] code: {}, message: {}", errorCode.name(), errorCode.getMessage());
        return new ResponseEntity<>(ErrorResponse.from(errorCode), errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<FieldErrorDetail> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDetail(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                ).toList();

        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.warn("[ValidationException] message: {}", defaultMessage);
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_INPUT_VALUE.name(),
                defaultMessage,
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {

        log.warn("[MaxUploadSizeException] 파일 크기 초과");
        ErrorCode errorCode = ErrorCode.EXCEED_MAX_UPLOAD_SIZE;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(Map.of("error", errorCode.getMessage()));
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyncTimeoutException(AsyncRequestTimeoutException e) {
        log.debug("SSE 연결 타임아웃");
    }

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException e, HttpServletRequest request) {

        String accept = request.getHeader("Accept");

        if (accept != null && accept.contains("text/event-stream")) {
            log.debug("[SSE 연결 정상 종료] {}", e.getMessage());
            return;
        }

        log.error("[IOException] {}", e.getMessage(), e);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public void handle404(HttpServletResponse response) throws IOException {
        response.sendRedirect("/error/404");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("[UnhandledException] {}", e.getMessage(), e);
        return new ResponseEntity<>(
                ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
