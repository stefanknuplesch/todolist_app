package com.campus02.todolist.model.users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsersService {
    @POST("/users")
    Call<UserDto> registerUser(@Body User user);

    @POST("/users/login")
    Call<UserDto> loginUser(@Body User user);
}
