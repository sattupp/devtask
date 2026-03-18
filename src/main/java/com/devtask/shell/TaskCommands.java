package com.devtask.shell;

import com.devtask.model.Task;
import com.devtask.service.TaskService;
import com.devtask.util.ColorPrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import java.util.List;

@ShellComponent   // Tells Spring Shell: scan this class for CLI commands
@RequiredArgsConstructor
public class TaskCommands {

    private final TaskService taskService;

    /**
     * COMMAND: add
     * Usage: add --title "Fix bug" --priority HIGH --tag backend --due 2025-03-01
     * Short: add -t "Fix bug" -p HIGH --tag backend -d 2025-03-01
     */
    @ShellMethod(key = "add", value = "Add a new task")
    public String add(
            @ShellOption(value = {"-t", "--title"},
                    help = "Task title (required)")
            String title,

            @ShellOption(value = {"-p", "--priority"},
                    defaultValue = "MEDIUM",
                    help = "Priority: LOW, MEDIUM, HIGH")
            String priority,

            @ShellOption(value = "--tag",
                    defaultValue = "",
                    help = "Tag for grouping (e.g. backend, docs)")
            String tag,

            @ShellOption(value = {"-d", "--due"},
                    defaultValue = "",
                    help = "Due date in yyyy-MM-dd format")
            String due
    ) {
        try {
            Task task = taskService.addTask(title, priority, tag, due);
            return ColorPrinter.success(
                    "Task #" + task.getId() + " added: \"" + task.getTitle() + "\""
            );
        } catch (IllegalArgumentException e) {
            return ColorPrinter.error("Invalid priority. Use: LOW, MEDIUM, or HIGH");
        } catch (Exception e) {
            return ColorPrinter.error("Failed: " + e.getMessage());
        }
    }

    /**
     * COMMAND: list
     * Usage: list           → pending tasks only
     *        list --all     → all tasks including done
     *        list --tag backend
     *        list --priority HIGH
     */
    @ShellMethod(key = "list", value = "List tasks")
    public String list(
            @ShellOption(value = {"-a", "--all"},
                    defaultValue = "false",
                    help = "Show all tasks including completed")
            boolean all,

            @ShellOption(value = "--tag",
                    defaultValue = "",
                    help = "Filter by tag")
            String tag,

            @ShellOption(value = {"-p", "--priority"},
                    defaultValue = "",
                    help = "Filter by priority")
            String priority
    ) {
        try {
            List<Task> tasks;
            if (!tag.isEmpty()) {
                tasks = taskService.getTasksByTag(tag);
            } else if (!priority.isEmpty()) {
                tasks = taskService.getTasksByPriority(priority);
            } else if (all) {
                tasks = taskService.getAllTasks();
            } else {
                tasks = taskService.getPendingTasks();
            }
            return ColorPrinter.buildTaskTable(tasks);
        } catch (Exception e) {
            return ColorPrinter.error("Error: " + e.getMessage());
        }
    }

    /**
     * COMMAND: done
     * Usage: done --id 3
     * Short: done -i 3
     */
    @ShellMethod(key = "done", value = "Mark a task as completed")
    public String done(
            @ShellOption(value = {"-i", "--id"}, help = "Task ID to complete")
            Long id
    ) {
        return taskService.completeTask(id)
                .map(t -> ColorPrinter.success("Task #" + id + " marked as done!"))
                .orElse(ColorPrinter.error("No task found with ID: " + id));
    }

    /**
     * COMMAND: delete
     * Usage: delete --id 2
     * Short: delete -i 2
     */
    @ShellMethod(key = "delete", value = "Delete a task")
    public String delete(
            @ShellOption(value = {"-i", "--id"}, help = "Task ID to delete")
            Long id
    ) {
        boolean deleted = taskService.deleteTask(id);
        return deleted
                ? ColorPrinter.success("Task #" + id + " deleted.")
                : ColorPrinter.error("No task found with ID: " + id);
    }

    /**
     * COMMAND: stats
     * Usage: stats
     */
    @ShellMethod(key = "stats", value = "Show task statistics dashboard")
    public String stats() {
        long total     = taskService.countTotal();
        long completed = taskService.countCompleted();
        long pending   = taskService.countPending();
        return ColorPrinter.buildStatsPanel(total, completed, pending);
    }

    /**
     * COMMAND: clear-done
     * Usage: clear-done
     * Deletes all completed tasks to clean up the list
     */
    @ShellMethod(key = "clear-done", value = "Delete all completed tasks")
    public String clearDone() {
        List<Task> done = taskService.getAllTasks().stream()
                .filter(Task::isCompleted)
                .toList();
        done.forEach(t -> taskService.deleteTask(t.getId()));
        return ColorPrinter.success("Cleared " + done.size() + " completed task(s).");
    }
}