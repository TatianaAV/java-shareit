package ru.practicum.shareit.exeption;

public class ErrorResponse {
    private final String error;
    StringBuffer request;

    public ErrorResponse(StringBuffer requestURL, String error) {
        this.error = error;
        this.request = requestURL;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public StringBuffer getRequest() {
        return request;
    }
}
