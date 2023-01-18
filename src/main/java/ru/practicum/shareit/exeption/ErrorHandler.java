package ru.practicum.shareit.exeption;

import com.sun.jdi.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exeption.model.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerValidationException(HttpServletRequest request, final ValidationException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("BAD_REQUEST {}", e.getMessage());
        return Map.of("Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlerNotFoundException(HttpServletRequest request, final NotFoundException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("404 NOT_FOUND {}", e.getMessage());
        return Map.of("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleFilmNotFoundException(HttpServletRequest request, final AlreadyExistException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("409 CONFLICT {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handlerInternalException(HttpServletRequest request, final InternalException e) {
        log.error("Requested URL=" + request.getRequestURL());
        log.error("500 CONFLICT {}", e.getMessage());
        return Map.of("Internal error", e.getMessage());
    }
}

