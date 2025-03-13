package com.example.meetime_test_app.builder;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiResponseErrorBuilder {

    private static Map<String, Object> createResponseBody(Integer statusCode, String reasonPhrase, String message, String requestURI) {
        Map<String, Object> errorBody = new LinkedHashMap<>();

        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", statusCode);
        errorBody.put("error", reasonPhrase);
        errorBody.put("message", message);
        errorBody.put("path", requestURI);

        return errorBody;
    }

    public static Map<String, Object> buildValidationResponseError(List<String> errors, HttpServletRequest request) {
        Map<String, Object> errorBody = createResponseBody(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed",
                request.getRequestURI()
        );

        errorBody.put("errors", errors);

        return errorBody;
    }

    public static Map<String, Object> buildHttpResponseError(ResponseStatusException ex, HttpServletRequest request) {
        return createResponseBody(
                ex.getStatusCode().value(),
                ex.getBody().getTitle(),
                ex.getReason(),
                request.getRequestURI()
        );
    }
}
