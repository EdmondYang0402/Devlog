package com.myproject.devlog.common;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void businessExceptionUsesSameHttpStatusAndBusinessCode() {
        ResponseEntity<Result<Void>> response = handler.handleBusinessException(
                new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getCode());
        assertEquals("用户名或密码错误", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void unknownExceptionReturnsSanitizedInternalServerError() {
        ResponseEntity<Result<Void>> response = handler.handleUnknownException(
                new IllegalStateException("database password leaked here"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("服务器内部错误", response.getBody().getMessage());
    }

    @Test
    void singleArgumentErrorDefaultsToBadRequestInsteadOfServerError() {
        Result<Void> result = Result.error("参数不合法");

        assertEquals(400, result.getCode());
        assertEquals("参数不合法", result.getMessage());
    }

    @Test
    void unknownDatabaseConstraintDoesNotLeakDetailsOrPretendToBeBadRequest() {
        ResponseEntity<Result<Void>> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("table=user password=secret"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("服务器内部错误", response.getBody().getMessage());
    }
}
