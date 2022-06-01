package com.campus02.todolist.model.tasks;

import android.util.Log;

import com.campus02.todolist.data.AppDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskManager {
    private final AppDatabase appDatabase;
    private final TasksService service;

    public TaskManager(AppDatabase appDatabase, TasksService service)
    {
        this.appDatabase = appDatabase;
        this.service = service;
    }

    public void fetchTasks (Integer userId) {
        service.fetchTasks(userId).enqueue(new Callback<List<FetchTaskInfo>>() {
            @Override
            public void onResponse(Call<List<FetchTaskInfo>> call, Response<List<FetchTaskInfo>> response) {
                Log.d("TASK_SYNC", Arrays.toString(response.body().toArray()));
                processFetchResponse(response.body(), userId);
            }
            @Override
            public void onFailure(Call<List<FetchTaskInfo>> call, Throwable t) {
                Log.e("TASK_SYNC", t.getMessage());
            }
        });
    }

    public void processFetchResponse (List<FetchTaskInfo> fetchResponse, Integer userId) {
        List<Task> dbTaskList = getDao().getAll(userId, true);
        SyncRequest syncRequest = new SyncRequest();
        List<UUID> deleteLocal = new ArrayList<>(); // TODO: implement
    /*
    Case 1: He has no entry for that id in his local database. Action: he needs to insert the entry in his database.
    Case 2: He has entry for that id in his local database. Action: Checks the timestamp,
            if it is newer than the server we will update the server entry otherwise we will update the local entry.
    Case 3: Mark has some ids in his database that are missing in the fetch.json.
            Action: Checks the flag ’synced’ if it is false this means the entry was created offline and we need to add it to our server,
                    if synced us true means the entry was deleted and Mark need to delete it locally too.
     */
        //
        // Schritt 1 --> Ermitteln der zu synchronisierenden Aufgaben:
        //
        for (FetchTaskInfo fti : fetchResponse) {
            Task localTask = dbTaskList.stream().filter(task -> task.getId().equals(fti.id)).findAny().orElse(null);
            // Case 1 -> lokalen eintrag erstellen
            if (localTask == null) {
                syncRequest.addToRetrieve(fti.id); // request full task!
            }
            // Case 2a -> server eintrag updaten
            else if (localTask.getLastModifiedTimestamp() > fti.lastModifiedTimestamp && !localTask.isDeleted()) {
                syncRequest.addToPersist(TaskDto.from(localTask));
            }
            // Case 2b -> lokalen eintrag updaten
            else if (localTask.getLastModifiedTimestamp() <= fti.lastModifiedTimestamp && !localTask.isDeleted()) {
                syncRequest.addToRetrieve(fti.id); // request full task!
            }
        }

        for (Task localTask : dbTaskList) {
            if (localTask.isDeleted()) {
                syncRequest.addToDelete(localTask.getId()); // lokalen Eintrag und am Server löschen
            }
            else if (fetchResponse.stream().noneMatch(fti -> fti.id.equals(localTask.getId()))) {
                // Case 3a --> lokalen Eintrag und am Server löschen
                if (localTask.isSynced()) {
                    deleteLocal.add(localTask.getId());
                }
                // Case 3b --> server eintrag erstellen (wurde offline erzeugt)
                else {
                    syncRequest.addToPersist(TaskDto.from(localTask));
                }
            }
        }

        //
        // Schritt 2 --> Sync Request absetzen
        //

        Gson gson = new Gson();
        Log.d("TASK_MANAGER_JSON_TO_DB", gson.toJson(syncRequest));
        service.syncTasks(userId, syncRequest).enqueue(new Callback<SyncResult>() {
            @Override
            public void onResponse(Call<SyncResult> call, Response<SyncResult> response) {
                // TODO
                Log.d("TASK_MANAGER_JSON_FROM_DB", gson.toJson(response.body()));
            }

            @Override
            public void onFailure(Call<SyncResult> call, Throwable t) {
                // TODO
            }
        });
    }

    public TaskDao getDao() { return appDatabase.taskDao(); }

}