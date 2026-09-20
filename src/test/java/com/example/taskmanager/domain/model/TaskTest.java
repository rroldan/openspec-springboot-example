package com.example.taskmanager.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskTest {

    @Test
    void rejectsBlankTitle() {
        assertThatThrownBy(() -> new Task(
                UUID.randomUUID(), " ", "description", TaskStatus.TODO,
                Instant.now(), Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requiresServerManagedLifecycleFields() {
        assertThatThrownBy(() -> new Task(
                UUID.randomUUID(), "title", "description", null,
                Instant.now(), Instant.now()))
                .isInstanceOf(NullPointerException.class);
    }
}
