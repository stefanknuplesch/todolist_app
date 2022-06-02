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
    private ValidationErrors errors;

    public Result(Response<T> response) {
        if (response.isSuccessful()) {
            value = response.body();
        }
        else {
            try {
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<ValidationErrors>>(){}.getType();
                List<ValidationError> errorsFromResponse = gson.fromJson(response.errorBody().string(), listType);

                errors = new ValidationErrors(errorsFromResponse);
            } catch (IOException e)
            {
                errors = new ValidationErrors("Unexpected error.");
            }
        }
    }

    public Boolean isSuccessful() { return value != null; }

    public T getValue() { return value; }

    public ValidationErrors getErrors() { return errors; }
}
