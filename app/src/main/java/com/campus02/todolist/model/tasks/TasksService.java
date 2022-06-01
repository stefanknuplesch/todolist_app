package com.campus02.todolist.model.tasks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TasksService {

    @GET("/tasks/fetch")
    Call<List<FetchTaskInfo>> fetchTasks(@Header("userId") Integer userId);

    @POST("/tasks/sync")
    Call<SyncResult> syncTasks(@Header("userId") Integer userId, @Body SyncRequest syncRequest);

}
