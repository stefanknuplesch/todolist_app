package com.campus02.todolist.activities.tasks;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus02.todolist.R;
import com.google.android.material.checkbox.MaterialCheckBox;

public class TaskHolder extends RecyclerView.ViewHolder {

  TextView task;

  public TaskHolder(@NonNull View itemView) {
    super(itemView);
    task = itemView.findViewById(R.id.tvTitle);
  }
}
