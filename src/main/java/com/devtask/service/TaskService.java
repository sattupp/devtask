package com.devtask.service;

import com.devtask.model.Task;
import com.devtask.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor  // Lombok: generates constructor injection for final fields
public class TaskService {

    private final TaskRepository taskRepository;

    /** Add a new task. Parses priority string and optional due date. */
    public Task addTask(String title, String priority, String tag, String dueDate) {
        Task.Priority p = Task.Priority.valueOf(priority.toUpperCase());

        // Parse due date only if provided (format: yyyy-MM-dd)
        LocalDate due = (dueDate != null && !dueDate.isEmpty())
                ? LocalDate.parse(dueDate)
                : null;

        Task task = Task.builder()
                .title(title)
                .priority(p)
                .tag((tag != null && !tag.isEmpty()) ? tag : null)
                .dueDate(due)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getPendingTasks() {
        return taskRepository.findByCompletedFalse();
    }

    public List<Task> getTasksByTag(String tag) {
        return taskRepository.findByTag(tag);
    }

    public List<Task> getTasksByPriority(String priority) {
        return taskRepository.findByPriority(
                Task.Priority.valueOf(priority.toUpperCase())
        );
    }

    /** Mark a task as completed. Returns Optional.empty() if task not found. */
    public Optional<Task> completeTask(Long id) {
        return taskRepository.findById(id).map(task -> {
            task.setCompleted(true);
            task.setCompletedAt(LocalDateTime.now());
            return taskRepository.save(task);
        });
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long countPending()   { return taskRepository.countByCompleted(false); }
    public long countCompleted() { return taskRepository.countByCompleted(true); }
    public long countTotal()     { return taskRepository.count(); }
}