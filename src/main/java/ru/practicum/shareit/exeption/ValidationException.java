package ru.practicum.shareit.exeption;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, String toString) {
        super(message + toString);
    }
}
