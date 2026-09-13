package com.example.taskmanager.application.service;

import com.example.taskmanager.domain.model.ApplicationInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationInfoServiceTest {

    @Test
    void returnsConfiguredApplicationIdentity() {
        ApplicationInfo info = new ApplicationInfoService("task-manager", "v1").getApplicationInfo();

        assertThat(info.service()).isEqualTo("task-manager");
        assertThat(info.version()).isEqualTo("v1");
    }
}
