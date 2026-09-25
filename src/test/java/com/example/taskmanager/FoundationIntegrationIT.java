package com.example.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FoundationIntegrationIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("taskmanager")
                    .withUsername("taskmanager")
                    .withPassword("taskmanager");

    @LocalServerPort
    private int port;

    @org.springframework.beans.factory.annotation.Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void startsWithFlywayAndExposesFoundationEndpoints() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        var api = client.send(request("/api/v1"), HttpResponse.BodyHandlers.ofString());
        var health = client.send(request("/actuator/health"), HttpResponse.BodyHandlers.ofString());

        assertThat(api.statusCode()).isEqualTo(200);
        assertThat(api.body()).contains("\"service\":\"task-manager\"", "\"version\":\"v1\"");
        assertThat(health.statusCode()).isEqualTo(200);
        assertThat(health.body()).contains("\"status\":\"UP\"");
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history", Integer.class)).isEqualTo(2);
    }

    @Test
    void createsTaskWithServerManagedLifecycleFields() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(HttpRequest.newBuilder(
                        URI.create("http://localhost:" + port + "/api/v1/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"title":"release notes","description":"summarize changes","status":"DONE"}
                        """))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).contains(
                "\"title\":\"release notes\"",
                "\"description\":\"summarize changes\"",
                "\"status\":\"TODO\"",
                "\"createdAt\"",
                "\"updatedAt\"");
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from tasks where title = 'release notes'", Integer.class)).isEqualTo(1);
    }

    @Test
    void documentsTaskCreationInOpenApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(request("/v3/api-docs"), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains(
                "\"/api/v1/tasks\"",
                "\"post\"",
                "\"201\"",
                "\"400\"",
                "CreateTaskRequest",
                "TaskResponse");
    }

    @Test
    void documentsTaskLookupInOpenApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(request("/v3/api-docs"), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains(
                "\"/api/v1/tasks/{taskId}\"",
                "\"getTaskById\"",
                "\"200\"",
                "\"404\"");
    }

    private HttpRequest request(String path) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
    }
}
