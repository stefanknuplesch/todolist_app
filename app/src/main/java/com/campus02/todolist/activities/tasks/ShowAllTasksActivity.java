package com.campus02.todolist.activities.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus02.todolist.R;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.tasks.RetrofitTasksServiceBuilder;
import com.campus02.todolist.model.tasks.Task;
import com.campus02.todolist.model.tasks.TasksService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllTasksActivity extends AppCompatActivity {
    private final int TEMP_USER_ID = 1;
    private RecyclerView rvTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_all_tasks);

        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddNewContact = findViewById(R.id.fabAddNewTask);
        fabAddNewContact.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddOrEditTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateTasks();
    }

    private void populateTasks() {
        TasksService tasksService = RetrofitTasksServiceBuilder.getTasksService();

        tasksService.getTaskOverview(TEMP_USER_ID).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                createAndSetTaskAdapter(response.body());
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(ShowAllTasksActivity.this, "Ups! Something went wrong :-(\nFailed to load tasks.", Toast.LENGTH_SHORT).show();
                createAndSetTaskAdapter(new ArrayList<>());
            }

            private void createAndSetTaskAdapter(List<Task> tasks) {
                TaskAdapter taskAdapter = getNewSortedAdapter(tasks);
                taskAdapter.setCallback((task, isChecked) -> {
                    task.setIsCompleted(isChecked);
                    tasksService
                            .updateTaskStatus(task.getId(), task.isCompleted(), TEMP_USER_ID)
                            .enqueue(new Callback<Task>() {
                                @Override
                                public void onResponse(Call<Task> call, Response<Task> response) {
                                    android.util.Log.d("ShowAllTasks", response.raw().toString());
                                    Result<Task> result = new Result<>(response);
                                    if (result.isSuccessful()) {
                                        Toast.makeText(ShowAllTasksActivity.this, "Aufgabe wurde erfolgreich aktualisiert.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ShowAllTasksActivity.this, "Fehler beim Aktualisieren der Aufgabe.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Task> call, Throwable t) {
                                    Toast.makeText(ShowAllTasksActivity.this, "Fehler beim Aktualisieren der Aufgabe.", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
                rvTasks.setAdapter(taskAdapter);
            }
        });
    }
    private TaskAdapter getNewSortedAdapter(List<Task> tasks) {
        Collections.sort(tasks);
        return new TaskAdapter(tasks);
    }
}