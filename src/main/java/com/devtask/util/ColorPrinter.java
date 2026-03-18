package com.devtask.util;

import com.devtask.model.Task;
import org.fusesource.jansi.Ansi;
import java.time.LocalDate;
import java.util.List;
import static org.fusesource.jansi.Ansi.ansi;

public class ColorPrinter {

    public static String success(String msg) {
        return ansi().fg(Ansi.Color.GREEN).bold().a("✔ ").reset()
                .fg(Ansi.Color.GREEN).a(msg).reset().toString();
    }

    public static String error(String msg) {
        return ansi().fg(Ansi.Color.RED).bold().a("✘ ").reset()
                .fg(Ansi.Color.RED).a(msg).reset().toString();
    }

    public static String info(String msg) {
        return ansi().fg(Ansi.Color.CYAN).a("ℹ  ").reset().a(msg).toString();
    }


    public static String buildTaskTable(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return ansi().fg(Ansi.Color.YELLOW).a("  No tasks found.").reset().toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        // Table header
        sb.append(ansi().bold().fg(Ansi.Color.CYAN).a(
                String.format("  %-4s %-36s %-8s %-12s %-12s %-10s%n",
                        "ID", "TITLE", "PRIORITY", "TAG", "DUE", "STATUS")
        ).reset());

        // Separator line
        sb.append(ansi().fgBright(Ansi.Color.BLACK)
                .a("  " + "─".repeat(86) + "\n").reset());

        for (Task task : tasks) {
            boolean overdue = task.getDueDate() != null
                    && task.getDueDate().isBefore(LocalDate.now())
                    && !task.isCompleted();

            String statusStr = task.isCompleted()
                    ? "✔ done"
                    : (overdue ? "⚠ overdue" : "○ todo");

            String due = task.getDueDate() != null ? task.getDueDate().toString() : "-";
            String tag = task.getTag() != null ? task.getTag() : "-";
            String title = truncate(task.getTitle(), 35);

            // Dim completed rows; red overdue rows
            if (task.isCompleted()) {
                sb.append(ansi().fgBright(Ansi.Color.BLACK));
            } else if (overdue) {
                sb.append(ansi().fg(Ansi.Color.RED));
            }

            sb.append(String.format("  %-4d %-36s ", task.getId(), title));
            sb.append(ansi().reset());

            // Color-coded priority badge
            sb.append(priorityBadge(task.getPriority()));

            if (task.isCompleted()) sb.append(ansi().fgBright(Ansi.Color.BLACK));
            else if (overdue)       sb.append(ansi().fg(Ansi.Color.RED));

            sb.append(String.format("  %-12s %-12s %-10s%n",
                    truncate(tag, 11), due, statusStr));
            sb.append(ansi().reset());
        }

        sb.append(ansi().fgBright(Ansi.Color.BLACK)
                .a("  " + "─".repeat(86) + "\n").reset());

        return sb.toString();
    }


    public static String buildStatsPanel(long total, long completed, long pending) {
        StringBuilder sb = new StringBuilder();
        double pct = total > 0 ? (completed * 100.0 / total) : 0;
        int bars = (int) (pct / 5);  // 20 total bar segments

        sb.append("\n");
        sb.append(ansi().bold().fg(Ansi.Color.CYAN)
                .a("  ══════════ DEVTASK STATS ══════════\n").reset());
        sb.append(ansi().fg(Ansi.Color.WHITE)
                .a(String.format("  Total tasks   : %d%n", total)).reset());
        sb.append(ansi().fg(Ansi.Color.GREEN)
                .a(String.format("  Completed     : %d%n", completed)).reset());
        sb.append(ansi().fg(Ansi.Color.YELLOW)
                .a(String.format("  Pending       : %d%n", pending)).reset());
        sb.append(ansi().fg(Ansi.Color.WHITE)
                .a(String.format("  Progress      : %.1f%%%n", pct)).reset());
        sb.append("\n  [");
        sb.append(ansi().fg(Ansi.Color.GREEN).a("█".repeat(bars)).reset());
        sb.append(ansi().fgBright(Ansi.Color.BLACK).a("░".repeat(20 - bars)).reset());
        sb.append(String.format("] %.0f%%%n", pct));
        sb.append(ansi().bold().fg(Ansi.Color.CYAN)
                .a("  ════════════════════════════════════\n").reset());

        return sb.toString();
    }


    private static String priorityBadge(Task.Priority priority) {
        return switch (priority) {
            case HIGH   -> ansi().fg(Ansi.Color.RED).bold().a("HIGH    ").reset().toString();
            case MEDIUM -> ansi().fg(Ansi.Color.YELLOW).bold().a("MEDIUM  ").reset().toString();
            case LOW    -> ansi().fg(Ansi.Color.GREEN).bold().a("LOW     ").reset().toString();
        };
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}