package com.campus02.todolist.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.campus02.todolist.model.tasks.Task;
import com.campus02.todolist.model.tasks.TaskDao;

@Database(entities = {Task.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "todoList")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract TaskDao taskDao();
}