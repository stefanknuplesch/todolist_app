package com.campus02.todolist.model.users;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUsersServiceBuilder {
    private final UsersService usersService;

    private RetrofitUsersServiceBuilder() {
        // At the moment we assume that we develop only in a simulator and that the
        // backend server and the simulator run on the same machine.
        // See: https://developer.android.com/studio/run/emulator-networking
        usersService = new Retrofit.Builder()
                .baseUrl("http://localhost:9000")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(UsersService.class);
    }

    private static RetrofitUsersServiceBuilder instance;
    public static UsersService getUsersService() {
        if (instance == null) instance = new RetrofitUsersServiceBuilder();
        return instance.usersService;
    }
}
