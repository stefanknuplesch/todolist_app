package com.campus02.todolist.model.tasks;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TasksService {

    @GET("/tasks/{id}")
    Call<Task> getTaskById(@Path("id") Integer taskId, @Header("userId") Integer userId);

    @GET("/tasks")
    Call<List<Task>> getTaskOverview(@Header("userId") Integer userId);

    @POST("/tasks")
    Call<Task> createTask(@Header("userId") Integer userId, @Body Task task);

    @PUT("/tasks/{id}")
    Call<Task> updateTask(@Path("id") Integer taskId, @Header("userId") Integer userId, @Body Task task);

    @PUT("/tasks/{id}/status")
    Call<Task> updateTaskStatus(@Path("id") Integer taskId, @Header("completed") Boolean isCompleted, @Header("userId") Integer userId);

    @DELETE("/tasks/{id}")
    Call<Task> deleteTask(@Path("id") Integer taskId, @Header("userId") Integer userId);


}
