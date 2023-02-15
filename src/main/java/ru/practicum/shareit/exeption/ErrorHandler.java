package ru.practicum.shareit.exeption;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handlerValidationException(HttpServletRequest request, final ValidationException e) {
        return new ErrorResponse(request.getRequestURL(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handlerNotFoundException(HttpServletRequest request, final NotFoundException e) {
        return new ErrorResponse(request.getRequestURL(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleFilmNotFoundException(HttpServletRequest request, final AlreadyExistException e) {
        return new ErrorResponse(request.getRequestURL(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalException(HttpServletRequest request, final HttpServerErrorException.InternalServerError e) {
        return new ErrorResponse(request.getRequestURL(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerServerErrorMessage(HttpServletRequest request, final SQLException e) {
        return new ErrorResponse(request.getRequestURL(), e.getMessage().substring(0, 50));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleAlreadyExistException(HttpServletRequest request, final IllegalStateException e) {
        return new ErrorResponse(request.getRequestURL(), e.getMessage());
    }
}
