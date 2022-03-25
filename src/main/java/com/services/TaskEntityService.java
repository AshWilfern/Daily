package com.services;

import com.entity.Task;
import com.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TaskEntityService implements EntityService<Task> {

    private TaskRepository taskRepository;

    @Autowired
    public TaskEntityService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getEntities() {
        List<Task> result = new ArrayList<>();
        taskRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public Task getEntityById(long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public void saveEntity(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void deleteEntity(long id) {
        taskRepository.deleteById(id);
    }
}
