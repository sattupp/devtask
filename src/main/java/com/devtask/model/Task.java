package com.devtask.model;

import jakarta.persistence.*;
        import lombok.*;
        import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data           // Lombok: generates getters, setters, equals, hashCode, toString
@Builder        // Lombok: lets us do Task.builder().title("...").build()
@NoArgsConstructor  // Lombok: JPA needs a no-arg constructor
@AllArgsConstructor // Lombok: constructor with all fields (used by @Builder)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // auto-incremented: 1, 2, 3...

    @Column(nullable = false)
    private String title;  // e.g. "Fix auth bug"

    @Enumerated(EnumType.STRING)  // stores "LOW"/"MEDIUM"/"HIGH" as text
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    private String tag;   // e.g. "backend", "docs"

    private LocalDate dueDate;  // e.g. 2025-03-01

    @Builder.Default
    private boolean completed = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;  // set when we mark as done

    // Inner enum: stored in the same file, referenced as Task.Priority
    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}