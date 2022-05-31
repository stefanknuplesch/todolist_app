package com.campus02.todolist.activities.tasks;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.campus02.todolist.R;
import com.campus02.todolist.activities.IntentExtras;
import com.campus02.todolist.model.Result;
import com.campus02.todolist.model.ValidationErrors;
import com.campus02.todolist.model.tasks.RetrofitTasksServiceBuilder;
import com.campus02.todolist.model.tasks.Task;
import com.campus02.todolist.model.tasks.TasksService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Request;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrEditTaskActivity extends AppCompatActivity {

  int TEMP_USER_ID = 1;

  TextInputEditText txtTitle;
  TextInputEditText txtDescription;
  MaterialCheckBox cbIsPublic;
  MaterialCheckBox cbIsCompleted;
  MaterialButton btnSave;
  MaterialButton btnCancel;

  Task task;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_or_edit_task);

    initializeComponents();
  }

  private void initializeComponents() {
    bindFormWidgets();

    int taskId = getIntent().getIntExtra(IntentExtras.TASK_ID, 0);
    boolean taskAlreadyExists = taskId != 0;

    if (taskAlreadyExists) {
      this.setTitle(R.string.edit_task);
      disableForm();
    }
    else {
      this.setTitle(R.string.create_task);
      enableForm();
    }

    TasksService tasksService = RetrofitTasksServiceBuilder.getTasksService();

    if (taskAlreadyExists) {
      tasksService
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
        });
    } else {
      task = new Task();
      populateFormFromTask(task);
    }

    btnSave.setOnClickListener(view -> {
      populateTaskFromForm(task);

      disableForm();

      if (taskAlreadyExists) {
        tasksService
          .updateTask(taskId, TEMP_USER_ID, task)
          .enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
              debug(req(response.raw().request()));
              debug(response.raw().toString());
              Result<Task> result = new Result<>(response);
              if (result.isSuccessful()) {
                Toast.makeText(AddOrEditTaskActivity.this, "Task successfully updated :-)", Toast.LENGTH_SHORT).show();
                finish();
              }
              else {
                displayValidationErrors(result.getErrors());
                enableForm();
              }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
              Toast.makeText(AddOrEditTaskActivity.this, "Ups! Something went wrong :-(\nFailed to save task.", Toast.LENGTH_SHORT).show();
              enableForm();
            }
          });
      }
      else {
        tasksService
          .createTask(TEMP_USER_ID, task)
          .enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
              debug(req(response.raw().request()));
              debug(response.raw().toString());
              Result<Task> result = new Result<>(response);
              if (result.isSuccessful()) {
                Toast.makeText(AddOrEditTaskActivity.this, "Task successfully added :-)\nAdd another one if you wish, or go back.", Toast.LENGTH_SHORT).show();
                enableForm();
                cleanForm();
                task = new Task();
              }
              else {
                displayValidationErrors(result.getErrors());
                enableForm();
              }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
              Toast.makeText(AddOrEditTaskActivity.this, "Ups! Something went wrong :-(\nFailed to add task.", Toast.LENGTH_SHORT).show();
              enableForm();
            }
          });
      }
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
    cbIsPublic = findViewById(R.id.cbIsPublic);
    btnSave = findViewById(R.id.btnSaveTask);
    btnCancel = findViewById(R.id.btnCancelTask);
  }

  private void enableForm() { enableOrDisableForm(true); }
  private void disableForm() { enableOrDisableForm(false); }

  private void enableOrDisableForm(boolean enable) {
    btnSave.setEnabled(enable);
    enableOrDisableEditText(txtTitle, enable);
    enableOrDisableEditText(txtDescription, enable);
  }

  private static void enableOrDisableEditText(EditText editText, boolean enable) {
    editText.setInputType(enable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
    editText.setFocusable(enable);
    editText.setFocusableInTouchMode(enable);
  }

  private void populateFormFromTask(Task task) {
    txtTitle.setText(task.getTitle());
    txtDescription.setText(task.getDescription());
    cbIsPublic.setChecked(task.isPublic());
    cbIsCompleted.setChecked(task.isCompleted());
  }

  private void populateTaskFromForm(Task task) {
    task.setTitle(String.valueOf(txtTitle.getText()));
    task.setDescription(String.valueOf(txtDescription.getText()));
    task.setIsCompleted(cbIsCompleted.isChecked());
    task.setIsPublic(cbIsPublic.isChecked());
  }

  private void cleanForm() {
    txtTitle.setText("");
    txtDescription.setText("");

    txtTitle.requestFocus();
  }

  // TODO: dann entfernen
  private void debug(String text) {
    android.util.Log.d("AddOrEditTaskActivity", text);
  }
  private String req(Request req){
   try {
     Request copy = req.newBuilder().build();
     Buffer buffer = new Buffer();
     if (copy.body() != null)
      copy.body().writeTo(buffer);
     return buffer.readUtf8();
   } catch (final IOException e) {
     return "did not work";
   }
  }
}