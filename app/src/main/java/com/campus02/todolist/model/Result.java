package com.campus02.todolist.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class Result<T> {

    private T value;
    private APIError error;

    public Result(Response<T> response) {
        if (response.isSuccessful()) {
            value = response.body();
        }
        else {
            try {
                Gson gson = new GsonBuilder().create();
                error = gson.fromJson(response.errorBody().string(), APIError.class);

            } catch (IOException e)
            {
                error = new APIError(418, "I'm a teapot", "Unexpected error");
            }
        }
    }

    public Boolean isSuccessful() { return value != null; }

    public T getValue() { return value; }

    public APIError getError() { return error; }

}
