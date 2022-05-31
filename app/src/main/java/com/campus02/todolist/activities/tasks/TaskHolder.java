package com.campus02.todolist.activities.tasks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campus02.todolist.R;

public class TaskHolder extends RecyclerView.ViewHolder {

  TextView title;

  public TaskHolder(@NonNull View itemView) {
    super(itemView);
    title = itemView.findViewById(R.id.tvTitle);
  }
}
