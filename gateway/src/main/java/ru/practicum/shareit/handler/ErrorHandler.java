package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handler(HttpServletRequest request, final Exception e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handlerValidationException(HttpServletRequest request, final MethodArgumentNotValidException e) {
        log.error("Requested URL= {}", request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Throwable e) {
        log.error("INTERNAL_SERVER_ERROR {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
