package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handlerValidationException(HttpServletRequest request, final Exception e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handlerNotFoundException(HttpServletRequest request, final NotFoundException e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalException(HttpServletRequest request, final HttpServerErrorException.InternalServerError e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerServerErrorMessage(HttpServletRequest request, final SQLException e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage().substring(0, 50));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleAlreadyExistException(HttpServletRequest request, final IllegalStateException e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
    /*@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(final ConstraintViolationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException e) {
        return new ErrorResponse(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Throwable e) {
        return new ErrorResponse(e.getMessage());
    }
}*/
}
