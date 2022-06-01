package com.campus02.todolist.activities.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.campus02.todolist.R;
import com.campus02.todolist.activities.Constants;
import com.campus02.todolist.activities.tasks.ShowAllTasksActivity;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.users.RetrofitUsersServiceBuilder;
import com.campus02.todolist.model.users.User;
import com.campus02.todolist.model.users.UsersService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin, btnRegister;
    UsersService usersService;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }

    void Login(){
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        usersService = RetrofitUsersServiceBuilder.getUsersService();

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        btnLogin.setOnClickListener(view -> {
            User loginUser = new User();
            loginUser.setEmail(etEmail.getText().toString());
            loginUser.setPassword(etPassword.getText().toString());

            usersService.loginUser(loginUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Result<User> result = new Result<>(response);
                    if (result.isSuccessful() && result.getValue().getId() != null) {
                        User user = result.getValue();
                        Toast.makeText(LoginActivity.this, "Login erfolgreich\nWillkommen " + user.getName(), Toast.LENGTH_SHORT).show();
                        sharedPreferences.edit().putInt(Constants.PREF_USERID, user.getId()).apply();
                        sharedPreferences.edit().putString(Constants.PREF_USERNAME, user.getName()).apply();
                        Intent intent = new Intent(LoginActivity.this, ShowAllTasksActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // TODO: Fehlerhandling
                        Toast.makeText(LoginActivity.this, "Login nicht erfolgreich. Email und Passwort prüfen!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Fehler beim Einloggen.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
