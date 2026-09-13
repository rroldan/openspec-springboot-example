package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.application.port.in.GetApplicationInfoUseCase;
import com.example.taskmanager.domain.model.ApplicationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private GetApplicationInfoUseCase useCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ApplicationController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsApplicationIdentity() throws Exception {
        when(useCase.getApplicationInfo()).thenReturn(new ApplicationInfo("task-manager", "v1"));

        mockMvc.perform(get("/api/v1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"service":"task-manager","version":"v1"}
                        """));
    }

    @Test
    void returnsJsonErrorForUnsupportedMethod() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("""
                        {"errorId":"METHOD_NOT_ALLOWED","message":"HTTP method is not supported for this resource"}
                        """, false))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString())
                .andExpect(header().exists("X-Correlation-Id"));
    }
}
