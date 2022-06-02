package com.campus02.todolist.model.tasks;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void mergeInto(List<Task> tasks);

    @Transaction
    @Query("UPDATE tasks SET isDeleted = 1 WHERE id = :id")
    void markDeleted(UUID id);

    @Transaction
    @Query("UPDATE tasks " +
            "SET isCompleted = :completed, lastModifiedTimestamp = :timestamp " +
            "WHERE id = :id")
    void markCompleted(UUID id, boolean completed, long timestamp);

    @Transaction
    @Query("UPDATE tasks SET isSynced = 1 WHERE id in (:idList)")
    void markSynced(List<UUID> idList);

    @Transaction
    @Query("DELETE FROM tasks WHERE id in (:idList)")
    void deleteByIds(List<UUID> idList);

}
