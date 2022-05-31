package com.campus02.todolist.model.tasks;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitTasksServiceBuilder {
    private final TasksService tasksService;

    private RetrofitTasksServiceBuilder() {
        // At the moment we assume that we develop only in a simulator and that the
        // backend server and the simulator run on the same machine.
        // See: https://developer.android.com/studio/run/emulator-networking
        tasksService = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9000")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(TasksService.class);
    }

    private static RetrofitTasksServiceBuilder instance;
    public static TasksService getTasksService() {
        if (instance == null) instance = new RetrofitTasksServiceBuilder();
        return instance.tasksService;
    }
}
