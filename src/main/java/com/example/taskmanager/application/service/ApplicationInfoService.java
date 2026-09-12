package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.in.GetApplicationInfoUseCase;
import com.example.taskmanager.domain.model.ApplicationInfo;

public class ApplicationInfoService implements GetApplicationInfoUseCase {

    private final String serviceName;
    private final String apiVersion;

    public ApplicationInfoService(String serviceName, String apiVersion) {
        this.serviceName = serviceName;
        this.apiVersion = apiVersion;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return new ApplicationInfo(serviceName, apiVersion);
    }
}
