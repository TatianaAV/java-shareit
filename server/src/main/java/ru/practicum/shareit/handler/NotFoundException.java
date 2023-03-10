package ru.practicum.shareit.handler;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}