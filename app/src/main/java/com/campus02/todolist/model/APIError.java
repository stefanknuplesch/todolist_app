package com.campus02.todolist.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

public class APIError {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public APIError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public int getStatusCode() {
        return status;
    }

    public String getStatusError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%d - %s: %s (on %s", status, error, message, path);
    }
}
