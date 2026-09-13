package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.application.port.in.GetApplicationInfoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Application", description = "Versioned application foundation endpoints")
public class ApplicationController {

    private final GetApplicationInfoUseCase getApplicationInfoUseCase;

    public ApplicationController(GetApplicationInfoUseCase getApplicationInfoUseCase) {
        this.getApplicationInfoUseCase = getApplicationInfoUseCase;
    }

    @GetMapping
    @Operation(summary = "Get application identity", description = "Returns the service name and current API version.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application identity",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "405", description = "Unsupported HTTP method",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ApplicationResponse getApplicationInfo() {
        return ApplicationResponse.from(getApplicationInfoUseCase.getApplicationInfo());
    }
}
