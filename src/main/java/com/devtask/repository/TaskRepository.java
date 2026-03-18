package com.devtask.repository;

import com.devtask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Spring reads the method name and generates:
    // SELECT * FROM tasks WHERE completed = false
    List<Task> findByCompletedFalse();

    // SELECT * FROM tasks WHERE tag = ?1
    List<Task> findByTag(String tag);

    // SELECT * FROM tasks WHERE priority = ?1
    List<Task> findByPriority(Task.Priority priority);

    // SELECT * FROM tasks WHERE completed = true
    List<Task> findByCompletedTrue();

    // SELECT COUNT(*) FROM tasks WHERE completed = ?1
    long countByCompleted(boolean completed);
}