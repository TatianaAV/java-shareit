package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public Map<HttpStatus, String> handlerValidationException(HttpServletRequest request, final ValidationException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("400 BAD_REQUEST {}", e.getMessage());
        return Map.of(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Map<HttpStatus, String> handlerNotFoundException(HttpServletRequest request, final NotFoundException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("404 NOT_FOUND {}", e.getMessage());
        return Map.of(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Map<HttpStatus, String> handleFilmNotFoundException(HttpServletRequest request, final AlreadyExistException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("409 CONFLICT {}", e.getMessage());
        return Map.of(CONFLICT, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public Map<HttpStatus, String> handlerInternalException(HttpServletRequest request, final HttpServerErrorException.InternalServerError e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("500 CONFLICT {}", e.getMessage());
        return Map.of(INTERNAL_SERVER_ERROR, Objects.requireNonNull(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public Map<HttpStatus, String> handlerServerErrorMessage(HttpServletRequest request, final SQLException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("500 CONFLICT {}", e.getMessage());
        log.error("SERVER_ERROR {}", e.getMessage());
        return Map.of(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(StatusBookingException.class)
    public ErrorResponse handleAlreadyExistException(HttpServletRequest request, final StatusBookingException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("500 CONFLICT StatusBookingException {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
