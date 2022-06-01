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
    @Query("SELECT * " +
            " FROM tasks " +
            "WHERE (originatorUserId = :userId OR isPublic = 1)" +
            "  AND (isDeleted = :includeDeleted OR :includeDeleted = 1)")
    List<Task> getAll(int userId, boolean includeDeleted);

    @Query("SELECT * FROM Tasks WHERE id = :id")
    Task getById(UUID id);

    @Insert
    void insert(Task... tasks);

    @Update
    void update(Task... tasks);

    @Query("UPDATE tasks SET isDeleted = 1 WHERE id = :id")
    void markDeleted(UUID id);

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    void markCompleted(UUID id, boolean completed);

    @Delete
    void delete(Task... tasks);

}
