package com.campus02.todolist.model.tasks;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks where originatorUserId = :userId or isPublic = 1")
    List<Task> getAll(int userId);

    @Query("SELECT * FROM Tasks WHERE id = :id")
    Task getById(UUID id);

    @Insert
    void insert(Task... tasks);

    @Update
    void update(Task... tasks);

    @Delete
    void delete(Task... tasks);
}
