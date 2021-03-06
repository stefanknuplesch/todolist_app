package com.campus02.todolist.activities.users;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.campus02.todolist.R;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.users.RetrofitUsersServiceBuilder;
import com.campus02.todolist.model.users.User;
import com.campus02.todolist.model.users.UserDto;
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

    private void Registration(){
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSubmit = findViewById(R.id.btnSubmit);
        usersService = RetrofitUsersServiceBuilder.getUsersService();

        btnSubmit.setOnClickListener(view -> {
            if (!validateInputs())
                return;
            User newUser = new User();
            newUser.setName(etUsername.getText().toString());
            newUser.setEmail(etEmail.getText().toString());
            newUser.setPassword(etPassword.getText().toString());

            usersService.registerUser(newUser).enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    Result<UserDto> result = new Result<>(response);
                    if (result.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Benutzer wurde erfolgreich registriert!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Log.e("REGISTRATION_ACTIVITY", result.getError().toString());
                        Toast.makeText(RegistrationActivity.this, "Fehler:\n" + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Log.e("REGISTRATION_ACTIVITY", t.getMessage());
                    Toast.makeText(RegistrationActivity.this, "Verbindung fehlgeschlagen: M??glicherweise ist der Server nicht erreichbar, versuchen Sie es sp??ter erneut.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean validateInputs() {
        if (etUsername.length() == 0) {
            etUsername.setError("Bitte geben Sie einen Benutzernamen ein!");
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText()) || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
            etEmail.setError("Bitte geben Sie eine g??ltige E-Mail Adresse ein!");
            return false;
        }
        if (etPassword.length() < 8) {
            etPassword.setError("Bitte geben Sie ein Passwort ein (min. 8 Zeichen)");
            return false;
        }
        return true;
    }
}

