package com.campus02.todolist.model.tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.campus02.todolist.data.AppDatabase;
import com.campus02.todolist.model.Result;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskManager {
    private final AppDatabase appDatabase;
    private final TasksService service;
    private SyncCompletedCallback syncCompletedCallback;

    public interface SyncCompletedCallback {
        void invoke();
    }

    public TaskManager(AppDatabase appDatabase)
    {
        this.appDatabase = appDatabase;
        this.service = RetrofitTasksServiceBuilder.getTasksService();
    }

    public void syncTasks(Context context, Integer userId) {
        service.fetchTasks(userId).enqueue(new Callback<List<FetchTaskInfo>>() {
            @Override
            public void onResponse(Call<List<FetchTaskInfo>> call, Response<List<FetchTaskInfo>> response) {
                Result<List<FetchTaskInfo>> result = new Result<>(response);
                if (result.isSuccessful()) {
                    processFetchResponse(context, response.body(), userId);
                }
                else {
                    Log.e("TASK_MANAGER_FETCH", result.getError().toString());
                    Toast.makeText(context, "Fehler beim Synchronisieren:\n" + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<FetchTaskInfo>> call, Throwable t) {
                Log.e("TASK_MANAGER_FETCH", t.getMessage());
                Toast.makeText(context, "Verbindung fehlgeschlagen: Möglicherweise ist der Server nicht erreichbar, versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void processFetchResponse (Context context, List<FetchTaskInfo> fetchResponse, Integer userId) {
        List<Task> localDbTaskList = getDao().getAll(true);
        SyncRequest syncRequest = new SyncRequest();
        List<UUID> collisionUuids = new ArrayList<>();
        List<UUID> deleteLocal = new ArrayList<>();

        //
        // Schritt 1 --> Ermitteln der zu synchronisierenden Aufgaben:
        // Geht sicher schöner :S
        //
        for (FetchTaskInfo fti : fetchResponse) {
            Task localTask = localDbTaskList.stream().filter(task -> task.getId().equals(fti.id)).findAny().orElse(null);
            // Case 1 -> lokalen eintrag erstellen
            if (localTask == null) {
                syncRequest.addToRetrieve(fti.id); // request full task!
            }
            // Case 2a -> server eintrag updaten
            else if (localTask.getLastModifiedTimestamp() > fti.lastModifiedTimestamp && !localTask.isDeleted()) {
                syncRequest.addToPersist(TaskDto.from(localTask));
            }
            // Case 2b -> lokalen eintrag updaten
            else if (localTask.getLastModifiedTimestamp() < fti.lastModifiedTimestamp && !localTask.isDeleted()) {
                collisionUuids.add(fti.id); // request full task if user wants to overwrite!
            }
        }

        for (Task localTask : localDbTaskList) {
            if (fetchResponse.stream().noneMatch(fti -> fti.id.equals(localTask.getId()))) {
                // Case 3a --> lokalen Eintrag und am Server löschen
                if (localTask.isSynced() || localTask.isDeleted()) {
                    deleteLocal.add(localTask.getId());
                }
                // Case 3b --> server eintrag erstellen (wurde offline erzeugt)
                else {
                    syncRequest.addToPersist(TaskDto.from(localTask));
                }
            }
            else if (localTask.isDeleted()) {
                syncRequest.addToDelete(localTask.getId()); // lokalen Eintrag und am Server löschen
            }
        }
        if (collisionUuids.isEmpty()) {
            doSyncRequest(context, syncRequest, userId, deleteLocal);
        }
        // Kollisionen vorhanden? User fragen
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Synchronisierung");
            alert.setMessage("Der Server verfügt im Vergleich zur lokalen Datenbank über aktuellere Daten.\n" +
                    "Sollen die betroffenen lokalen Daten überschrieben werden?");
            alert.setPositiveButton("Überschreiben", (dialog, which) -> {
                syncRequest.addAllToRetrieve(collisionUuids);
                doSyncRequest(context, syncRequest, userId, deleteLocal);
            });
            alert.setNegativeButton("Behalten", (dialog, which)
                    -> doSyncRequest(context, syncRequest, userId, deleteLocal));
            alert.setNeutralButton("Abbrechen", (dialog, which) -> dialog.cancel());
            alert.show();
        }
    }
    public TaskDao getDao() { return appDatabase.taskDao(); }

    public void setSyncCompletedCallback(SyncCompletedCallback cb) {
        this.syncCompletedCallback = cb;
    }

    private void doSyncRequest(Context context, SyncRequest syncRequest, Integer userId, List<UUID> deleteLocal) {
        Gson gson = new Gson();
        Log.d("TASK_MANAGER_JSON_TO_DB", gson.toJson(syncRequest));
        service.syncTasks(userId, syncRequest).enqueue(new Callback<SyncResult>() {
            @Override
            public void onResponse(Call<SyncResult> call, Response<SyncResult> response) {
                Log.d("TASK_MANAGER_JSON_FROM_DB", gson.toJson(response.body()));
                Result<SyncResult> result = new Result<>(response);
                if (result.isSuccessful() && result.getValue() != null) {
                    SyncResult res = response.body();

                    // 1) Daten vom Server in die lokale DB einfügen
                    appDatabase.taskDao().mergeInto(res.retrieved.stream().map(TaskDto::toSync).collect(Collectors.toList()));

                    // 2) Am Server eingefügte Daten auf sync=true setzen
                    appDatabase.taskDao().markSynced(res.persisted);

                    // 3) Am Server gelöschte Daten und lokal übriggebliebene endgültig löschen
                    appDatabase.taskDao().deleteByIds(Stream.concat(res.deleted.stream(), deleteLocal.stream()).collect(Collectors.toList()));

                    syncCompletedCallback.invoke();
                    Toast.makeText(context, "Daten wurden erfolgreich synchronisiert!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TASK_MANAGER_SYNC", result.getError().toString());
                    Toast.makeText(context, "Fehler:\n" + result.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SyncResult> call, Throwable t) {
                Log.e("TASK_MANAGER_SYNC", t.getMessage());
                Toast.makeText(context, "Verbindung fehlgeschlagen: Möglicherweise ist der Server nicht erreichbar, versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
            }
        });
    }

}