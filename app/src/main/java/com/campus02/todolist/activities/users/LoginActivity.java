package com.campus02.todolist.activities.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.campus02.todolist.R;
import com.campus02.todolist.activities.Constants;
import com.campus02.todolist.activities.tasks.ShowAllTasksActivity;
import com.campus02.todolist.model.APIError;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.users.UserDto;
import com.campus02.todolist.model.users.RetrofitUsersServiceBuilder;
import com.campus02.todolist.model.users.User;
import com.campus02.todolist.model.users.UsersService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin, btnRegister;
    UsersService usersService;
    boolean isValid;

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
            if (!validateInputs())
                return;

            User loginUser = new User();
            loginUser.setEmail(etEmail.getText().toString());
            loginUser.setPassword(etPassword.getText().toString());

            usersService.loginUser(loginUser).enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    Result<UserDto> result = new Result<>(response);
                    if (result.isSuccessful() && result.getValue().getId() != null) {
                        processLogin(result.getValue());
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Fehler:\n" + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Verbindung fehlgeschlagen: Möglicherweise ist der Server nicht erreichbar, versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
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

    private void processLogin(UserDto user)
    {
        Toast.makeText(LoginActivity.this, "Willkommen " + user.getName() + "!", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().putInt(Constants.PREF_USERID, user.getId()).apply();
        sharedPreferences.edit().putString(Constants.PREF_USERNAME, user.getName()).apply();
        Intent intent = new Intent(LoginActivity.this, ShowAllTasksActivity.class);
        startActivity(intent);
        finish();
    }


    private boolean validateInputs() {
        if (TextUtils.isEmpty(etEmail.getText()) || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
            etEmail.setError("Bitte geben Sie eine gültige E-Mail Adresse ein.");
            return false;
        }

        if (etPassword.length() == 0) {
            etPassword.setError("Bitte geben Sie ihr Passwort ein.");
            return false;
        }

        return true;
    }
}
