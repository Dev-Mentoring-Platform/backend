package com.project.mentoridge.config.controllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestControllerExceptionAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error("===================== RuntimeException Handling =====================");
        log.error("RuntimeException", e);
        // e.printStackTrace();
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생하였습니다.", Collections.singletonList(e.getMessage()));
    }

//    @ExceptionHandler(Exception.class)
//    public ErrorResponse handleException(Exception e) {
//        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생하였습니다.", Collections.singletonList(e.getMessage()));
//    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException", e);
        return ErrorResponse.of(ErrorCode.UNAUTHENTICATED, e.getMessage());
    }
/*
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenExpiredException.class)
    public ErrorResponse handleTokenExpiredException(TokenExpiredException e) {
        log.error("TokenExpiredException", e);
        return ErrorResponse.of(ErrorCode.TOKEN_EXPIRED);
    }*/

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException", e);
        return ErrorResponse.of(ErrorCode.ENTITY_NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponse handleUnauthorizedException(UnauthorizedException e) {
        // log.error("UnauthorizedException", e);
        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistException.class)
    public ErrorResponse handleAlreadyExistException(AlreadyExistException e) {
        log.error("AlreadyExistException", e);
        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindException(BindException e) {
        log.error("===================== BindException Handling =====================");
        log.error("BindException", e);
        // e.printStackTrace();
        return getErrorResponse(e.getBindingResult(), HttpStatus.BAD_REQUEST, "유효하지 않는 값이 있습니다.");
    }

    private ErrorResponse getErrorResponse(BindingResult bindingResult, HttpStatus httpStatus, String defaultMessage) {
        List<String> errorMessages = Optional.ofNullable(bindingResult.getAllErrors()).orElse(Collections.emptyList())
                .stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
        return ErrorResponse.of(httpStatus.value(), defaultMessage, errorMessages);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        return ErrorResponse.of(ErrorCode.INVALID_INPUT, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
