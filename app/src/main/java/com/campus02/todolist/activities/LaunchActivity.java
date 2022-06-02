package com.campus02.todolist.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.campus02.todolist.activities.tasks.ShowAllTasksActivity;
import com.campus02.todolist.activities.users.LoginActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        if (hasUserInfo(sharedPreferences)) {
            activityIntent = new Intent(this, ShowAllTasksActivity.class);
        }
        else {
            activityIntent = new Intent(this, LoginActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }

    private boolean hasUserInfo(SharedPreferences sp)
    {
        return sp.contains(Constants.PREF_USERID) &&
                sp.getInt(Constants.PREF_USERID, -1) > 0 &&
                sp.contains(Constants.PREF_USERNAME) &&
                !sp.getString(Constants.PREF_USERNAME, "").isEmpty();
    }
}