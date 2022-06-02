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
                taskManager.syncTasks(this, userId);
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