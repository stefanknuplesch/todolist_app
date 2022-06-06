package com.campus02.todolist.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.campus02.todolist.activities.Constants;
import com.campus02.todolist.model.tasks.Task;
import com.campus02.todolist.model.tasks.TaskDao;

@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase dbInstance;

    public static AppDatabase getInstance(Context context, int userId) {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME + "_" + userId)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbInstance;
    }

    public static void destroyInstance() {
        if (dbInstance.isOpen()) {
            dbInstance.close();
        }
        dbInstance = null;
    }

    public abstract TaskDao taskDao();
}