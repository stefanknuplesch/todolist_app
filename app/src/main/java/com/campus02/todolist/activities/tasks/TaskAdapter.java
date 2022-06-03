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
import java.util.UUID;

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
    holder.task.setTextAppearance(getTextAppearance(task.isCompleted(), task.isPublic()));
    holder.itemView.setTag(task.getId());
    holder.itemView.setOnClickListener(itemView -> {
      UUID taskId = (UUID)itemView.getTag();
      Intent intent = new Intent(itemView.getContext(), AddOrEditTaskActivity.class);
      intent.putExtra(IntentExtras.TASK_ID, taskId);
      itemView.getContext().startActivity(intent);
    });
    holder.completed.setOnCheckedChangeListener((cb, isChecked) -> {
      if (callback != null) {
        callback.onCheckedChanged(cb, isChecked);
        notifyItemChanged(position, getTextAppearance(isChecked, tasks.get(position).isPublic()));
      }
    });
  }

  @Override
  public void onBindViewHolder(TaskHolder holder, int position, List<Object> payloads) {
    if(!payloads.isEmpty()) {
      if (payloads.get(0) instanceof Integer) {
        holder.task.setTextAppearance((Integer) payloads.get(0));
      }
    }
    else {
      super.onBindViewHolder(holder, position, payloads);
    }
  }

  @Override
  public int getItemCount() {
    return tasks.size();
  }

  public interface Callback {
    void onCheckedChanged(View view, boolean isChecked);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  private Integer getTextAppearance (boolean isCompleted, boolean isPublic) {
    if (isPublic) {
      if (isCompleted) {
        return R.style.isPublicAndCompleted;
      }
      else {
        return R.style.isPublicAndNotCompleted;
      }
    }
    else
    {
      if (isCompleted) {
        return R.style.isCompleted;
      }
      else {
        return R.style.isNotCompleted;
      }
    }
  }
}
