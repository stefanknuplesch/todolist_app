package com.campus02.todolist.activities.tasks;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.campus02.todolist.R;
import com.campus02.todolist.activities.IntentExtras;
import com.campus02.todolist.model.tasks.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

  private List<Task> tasks;
  private Callback callback;

  public TaskAdapter(List<Task> tasks) {
    this.tasks = tasks;
  }

  @Override
  public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_task, parent, false);
    return new TaskHolder(view);
  }

  @Override
  public void onBindViewHolder(TaskHolder holder, int position) {
    Task task = tasks.get(position);
    holder.task.setText(task.getTitle());
    holder.completed.setChecked(task.isCompleted());
    holder.itemView.setTag(task.getId());
    holder.itemView.setOnClickListener(itemView -> {
      int taskId = (int)itemView.getTag();
      Intent intent = new Intent(itemView.getContext(), AddOrEditTaskActivity.class);
      intent.putExtra(IntentExtras.TASK_ID, taskId);
      itemView.getContext().startActivity(intent);
    });
    holder.completed.setOnCheckedChangeListener((cb, isChecked) -> {
      if (callback != null) {
        callback.onCheckedChanged(task, isChecked);
      }
    });
  }

  @Override
  public int getItemCount() {
    return tasks.size();
  }

  public interface Callback {
    void onCheckedChanged(Task task, boolean isChecked);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }
}
