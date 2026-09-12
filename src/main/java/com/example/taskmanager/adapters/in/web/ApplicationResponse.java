package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.domain.model.ApplicationInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identity and API version of the running service")
public record ApplicationResponse(
        @Schema(example = "task-manager") String service,
        @Schema(example = "v1") String version) {

    public static ApplicationResponse from(ApplicationInfo applicationInfo) {
        return new ApplicationResponse(applicationInfo.service(), applicationInfo.version());
    }
}
