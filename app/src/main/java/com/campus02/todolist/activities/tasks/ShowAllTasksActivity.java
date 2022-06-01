package com.campus02.todolist.activities.tasks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus02.todolist.R;
import com.campus02.todolist.activities.Constants;
import com.campus02.todolist.activities.users.LoginActivity;
import com.campus02.todolist.data.AppDatabase;
import com.campus02.todolist.model.tasks.RetrofitTasksServiceBuilder;
import com.campus02.todolist.model.tasks.Task;
import com.campus02.todolist.model.tasks.TaskManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class ShowAllTasksActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TaskManager taskManager;
    private int userId;
    private RecyclerView rvTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_all_tasks);
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        initializeComponents();

        taskManager = new TaskManager(AppDatabase.getInstance(this), RetrofitTasksServiceBuilder.getTasksService());
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        userId = sharedPreferences.getInt(Constants.PREF_USERID, -1);

        taskManager.fetchTasks(userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optSynchronize:
                // TODO: synchronisieren per Button
                return true;
            case R.id.optLogout:
                doLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogout() {
        String loggedOutUsername = sharedPreferences.getString(Constants.PREF_USERNAME, "");
        sharedPreferences.edit().remove(Constants.PREF_USERID).apply();
        sharedPreferences.edit().remove(Constants.PREF_USERNAME).apply();

        if (!loggedOutUsername.isEmpty()) {
            Toast.makeText(this, loggedOutUsername + " wurde erfolgreich ausgeloggt.", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeComponents() {
        FloatingActionButton fabAddNewContact = findViewById(R.id.fabAddNewTask);
        fabAddNewContact.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddOrEditTaskActivity.class);
            startActivity(intent);
        });
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateTasksFromDatabase();
    }

    private void populateTasksFromDatabase() {
        /*TasksService tasksService = RetrofitTasksServiceBuilder.getTasksService();

        tasksService.getTaskOverview(TEMP_USER_ID).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                createAndSetTaskAdapter(response.body());
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(ShowAllTasksActivity.this, "Fehler beim Laden der Tasks.", Toast.LENGTH_SHORT).show();
                createAndSetTaskAdapter(new ArrayList<>());
            }

            private void createAndSetTaskAdapter(List<Task> tasks) {
                Collections.sort(tasks);
                TaskAdapter taskAdapter = new TaskAdapter(tasks);
                taskAdapter.setCallback((task, isChecked) -> {
                    task.setCompleted(isChecked);
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
        });*/
        List<Task> tasks = taskManager.getDao().getAll(userId, false);
        createAndSetTaskAdapter(tasks);

    }
    private void createAndSetTaskAdapter(List<Task> tasks) {
        Collections.sort(tasks);
        TaskAdapter taskAdapter = new TaskAdapter(tasks);
        taskAdapter.setCallback((task, isChecked) -> {
            taskManager.getDao().markCompleted(task.getId(), isChecked);
        });
        rvTasks.setAdapter(taskAdapter);
    }

}