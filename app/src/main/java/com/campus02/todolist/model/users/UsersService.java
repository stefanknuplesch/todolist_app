package com.campus02.todolist.model.users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsersService {
    @POST("/users/login")
    Call<User> loginUser(@Body User user);

    @PUT("/users/register")
    Call<User> registerUser(@Body User user);
}
