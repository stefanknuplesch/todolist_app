package com.campus02.todolist.model.tasks;

import java.util.List;
import java.util.UUID;

public class SyncResult {
    public List<UUID> persisted;
    public List<TaskDto> retrieved;
    public List<UUID> deleted;
}
