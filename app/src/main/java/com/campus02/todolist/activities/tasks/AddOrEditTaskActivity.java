package com.campus02.todolist.activities.tasks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.campus02.todolist.R;
import com.campus02.todolist.activities.IntentExtras;
import com.campus02.todolist.data.AppDatabase;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.ValidationErrors;
import com.campus02.todolist.model.tasks.RetrofitTasksServiceBuilder;
import com.campus02.todolist.model.tasks.Task;
import com.campus02.todolist.model.tasks.TasksService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Request;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrEditTaskActivity extends AppCompatActivity {

  int TEMP_USER_ID = 1;

  TextInputEditText txtTitle;
  TextInputEditText txtDescription;
  MaterialCheckBox cbIsCompleted;
  MaterialButton btnSave;
  MaterialButton btnDelete;
  RadioGroup rgIsPublic;

  Task task;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_or_edit_task);

    initializeComponents();
  }

  private void initializeComponents() {
    bindFormWidgets();

    UUID taskId = (UUID)getIntent().getSerializableExtra(IntentExtras.TASK_ID);
    boolean taskAlreadyExists = taskId != null;

    if (taskAlreadyExists) {
      this.setTitle(R.string.edit_task);
      disableForm();
    }
    else {
      this.setTitle(R.string.create_task);
      enableForm();
      btnDelete.setEnabled(false);
    }

    //TasksService tasksService = RetrofitTasksServiceBuilder.getTasksService();
    AppDatabase db = AppDatabase.getInstance(this);

    if (taskAlreadyExists) {
      /*tasksService
        .getTaskById(taskId, TEMP_USER_ID)
        .enqueue(new Callback<Task>() {
          @Override
          public void onResponse(Call<Task> call, Response<Task> response) {
            debug(req(response.raw().request()));
            debug(response.raw().toString());
            Result<Task> result = new Result<>(response);
            if (result.isSuccessful()) {
              task = result.getValue();
              debug(task.toString());
              populateFormFromTask(task);
              enableForm();
            }
            else {
              String errorMessage = result.getErrors().errorsWithoutPropertiesAsString();
              Toast.makeText(AddOrEditTaskActivity.this, "Ups! Something went wrong :-(\n" + errorMessage, Toast.LENGTH_SHORT).show();
              task = new Task();
            }
          }

          @Override
          public void onFailure(Call<Task> call, Throwable t) {
            Toast.makeText(AddOrEditTaskActivity.this, "Ups! Something went wrong :-(\nFailed to load task.", Toast.LENGTH_SHORT).show();
            task = new Task();
          }
        });*/
      task = db.taskDao().getById(taskId);
      if (task != null) {
        populateFormFromTask(task);
        enableForm();
      }
      else {
        task = new Task();
      }

    } else {
      task = new Task();
      populateFormFromTask(task);
    }

    btnSave.setOnClickListener(view -> {
      populateTaskFromForm(task);
      disableForm();

      if (taskAlreadyExists) {
        /*tasksService
          .updateTask(taskId, TEMP_USER_ID, task)
          .enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
              debug(req(response.raw().request()));
              debug(response.raw().toString());
              Result<Task> result = new Result<>(response);
              if (result.isSuccessful()) {
                Toast.makeText(AddOrEditTaskActivity.this, "Aufgabe wurde erfolgreich aktualisiert.", Toast.LENGTH_SHORT).show();
                finish();
              }
              else {
                displayValidationErrors(result.getErrors());
                enableForm();
              }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
              Toast.makeText(AddOrEditTaskActivity.this, "Fehler beim Aktualisieren der Aufgabe.", Toast.LENGTH_SHORT).show();
              enableForm();
            }
          });*/
        task.setLastModifiedUserId(TEMP_USER_ID);
        task.setLastModifiedTimestamp(System.currentTimeMillis());
        db.taskDao().update(task);
        Toast.makeText(AddOrEditTaskActivity.this, "Aufgabe wurde erfolgreich aktualisiert.", Toast.LENGTH_SHORT).show();
        finish();
      }
      else {
        /*tasksService
          .createTask(TEMP_USER_ID, task)
          .enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
              debug(req(response.raw().request()));
              debug(response.raw().toString());
              Result<Task> result = new Result<>(response);
              if (result.isSuccessful()) {
                Toast.makeText(AddOrEditTaskActivity.this, "Aufgabe wurde erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
                enableForm();
                cleanForm();
                task = new Task();
                btnDelete.setEnabled(false);
              }
              else {
                displayValidationErrors(result.getErrors());
                enableForm();
                btnDelete.setEnabled(false);
              }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
              Toast.makeText(AddOrEditTaskActivity.this, "Fehler beim Erstellen der Aufgabe.", Toast.LENGTH_SHORT).show();
              enableForm();
              btnDelete.setEnabled(false);
            }
          });*/
        task.setId(UUID.randomUUID());
        task.setOriginatorUserId(TEMP_USER_ID);
        task.setLastModifiedUserId(TEMP_USER_ID);
        task.setLastModifiedTimestamp(System.currentTimeMillis());
        db.taskDao().insert(task);
        Toast.makeText(AddOrEditTaskActivity.this, "Aufgabe wurde erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
        enableForm();
        cleanForm();
        task = new Task();
        btnDelete.setEnabled(false);
      }
    });

    btnDelete.setOnClickListener(view -> {
      if (!taskAlreadyExists)
        return;
      AlertDialog.Builder alert = new AlertDialog.Builder(this);
      alert.setTitle("Aufgabe löschen");
      alert.setMessage("Soll die Aufgabe wirklich gelöscht werden?");
      alert.setPositiveButton("Ja", (dialog, which) -> {
        /*tasksService.deleteTask(taskId, TEMP_USER_ID).enqueue(new Callback<Task>() {
          @Override
          public void onResponse(Call<Task> call, Response<Task> response) {
            debug(req(response.raw().request()));
            debug(response.raw().toString());
            Result<Task> result = new Result<>(response);
            if (result.isSuccessful()) {
              Toast.makeText(AddOrEditTaskActivity.this, "Aufgabe wurde erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
              finish();
            }
            else {
              displayValidationErrors(result.getErrors());
              enableForm();
            }
          }
          @Override
          public void onFailure(Call<Task> call, Throwable t) {
            Toast.makeText(AddOrEditTaskActivity.this, "Fehler beim Löschen der Aufgabe.", Toast.LENGTH_SHORT).show();
            enableForm();
          }*/
        db.taskDao().markDeleted(task.getId());
        Toast.makeText(AddOrEditTaskActivity.this, "Aufgabe wurde erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
        finish();
        });
      alert.setNegativeButton("Nein", (dialog, which) -> dialog.cancel());
      alert.show();
    });
  }

  private void displayValidationErrors(ValidationErrors errors) {
    txtTitle.setError(null);
    txtDescription.setError(null);

    errors.errorsWithProperties().forEach(error -> {
      TextInputEditText editText = null;

      switch (error.property) {
        case "title" : editText = txtTitle; break;
        case "description" : editText = txtDescription; break;
      }

      if (editText != null) editText.setError(error.message);
    });

    String errorMessage = errors.errorsWithoutPropertiesAsString();
    if (!errorMessage.isEmpty())
      Toast.makeText(AddOrEditTaskActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
  }

  private void bindFormWidgets() {
    txtTitle = findViewById(R.id.txtTitle);
    txtDescription = findViewById(R.id.txtDescription);
    cbIsCompleted = findViewById(R.id.cbIsCompleted);
    rgIsPublic = findViewById(R.id.rgIsPublic);
    btnSave = findViewById(R.id.btnSaveTask);
    btnDelete = findViewById(R.id.btnDeleteTask);
  }

  private void enableForm() { enableOrDisableForm(true); }
  private void disableForm() { enableOrDisableForm(false); }

  private void enableOrDisableForm(boolean enable) {
    enableOrDisableEditText(txtTitle, enable);
    enableOrDisableEditText(txtDescription, enable);
    cbIsCompleted.setEnabled(enable);
    btnSave.setEnabled(enable);
    btnDelete.setEnabled(enable);
    enableOrDisableRadioGroup(rgIsPublic, enable);
  }

  private static void enableOrDisableEditText(EditText editText, boolean enable) {
    editText.setFocusable(enable);
    editText.setFocusableInTouchMode(enable);
  }

  private static void enableOrDisableRadioGroup(RadioGroup radioGroup, boolean enable) {
    radioGroup.setFocusable(enable);
    radioGroup.setFocusableInTouchMode(enable);
    for(int i = 0; i < radioGroup.getChildCount(); i++) {
      radioGroup.getChildAt(i).setEnabled(enable);
    }
  }

  private void populateFormFromTask(Task task) {
    txtTitle.setText(task.getTitle());
    txtDescription.setText(task.getDescription());
    cbIsCompleted.setChecked(task.isCompleted());
    rgIsPublic.check(getIsPublicRadioId(task.isPublic()));
  }

  private void populateTaskFromForm(Task task) {
    task.setTitle(String.valueOf(txtTitle.getText()));
    task.setDescription(String.valueOf(txtDescription.getText()));
    task.setCompleted(cbIsCompleted.isChecked());
    task.setPublic(getIsPublicValue());
  }

  private void cleanForm() {
    txtTitle.setText("");
    txtDescription.setText("");
    txtTitle.requestFocus();
  }

  private boolean getIsPublicValue() {
    int selectedId = rgIsPublic.getCheckedRadioButtonId();
    switch(selectedId) {
      case R.id.rbPublic:
        return true;
      default:
        return false;
    }
  }

  private int getIsPublicRadioId(boolean isPublic) {
    if (isPublic)
      return R.id.rbPublic;
    else
      return R.id.rbPrivate;
  }
}