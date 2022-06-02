package com.campus02.todolist.activities.users;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.campus02.todolist.R;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.users.RetrofitUsersServiceBuilder;
import com.campus02.todolist.model.users.User;
import com.campus02.todolist.model.users.UsersService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etUsername;
    MaterialButton btnSubmit;
    UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Registration();
    }

    void Registration(){
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSubmit = findViewById(R.id.btnSubmit);
        usersService = RetrofitUsersServiceBuilder.getUsersService();

        btnSubmit.setOnClickListener(view -> {
            User newUser = new User();
            newUser.setName(etUsername.getText().toString());
            newUser.setEmail(etEmail.getText().toString());
            newUser.setPassword(etPassword.getText().toString());

            usersService.registerUser(newUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    android.util.Log.d("RegistrationActivity", response.raw().toString());
                    Result<User> result = new Result<>(response);
                    // TODO issuccessful handling
                    if (result.isSuccessful() && result.getValue().getId() != null) {
                        Toast.makeText(RegistrationActivity.this, "Benutzer erfolgreich registriert", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        // TODO: Fehlerhandling
                        Toast.makeText(RegistrationActivity.this, "Login nicht erfolgreich. Email und Passwort prüfen!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(RegistrationActivity.this, "Fehler beim Registrieren.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
