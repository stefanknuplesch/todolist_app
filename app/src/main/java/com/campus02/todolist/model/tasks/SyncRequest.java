package com.campus02.todolist.model.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncRequest {
    private List<UUID> toRetrieve = new ArrayList<>(); // get more info from api
    private List<TaskDto> toPersist = new ArrayList<>(); // insert/update data at server
    private List<UUID> toDelete = new ArrayList<>(); // delete local and server data

    public void addToRetrieve(UUID item) {
        toRetrieve.add(item);
    }
    public void addToPersist(TaskDto item) {
        toPersist.add(item);
    }
    public void addToDelete(UUID item) {
        toDelete.add(item);
    }
}
