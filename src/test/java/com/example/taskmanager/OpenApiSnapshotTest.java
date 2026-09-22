package com.example.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenApiSnapshotTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void rejectsInvalidJson() {
        assertThat(OpenApiSnapshot.invalidJsonMessage(mapper, "{invalid")).isEqualTo(
                "OpenAPI response was not valid JSON");
    }

    @Test
    void rejectsEmptyDocument() {
        assertThatThrownBy(() -> OpenApiSnapshot.normalize(mapper, " \n"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OpenAPI response was empty");
    }

    @Test
    void detectsStaleSnapshot(@TempDir Path tempDir) throws IOException {
        Path snapshot = tempDir.resolve("docs/openapi.json");
        OpenApiSnapshot.writeOrVerify(snapshot, "{\n  \"openapi\" : \"3.1.0\"\n}\n", true);

        assertThatThrownBy(() -> OpenApiSnapshot.writeOrVerify(
                snapshot, "{\n  \"openapi\" : \"3.1.1\"\n}\n", false))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("OpenAPI snapshot is stale");
    }

    @Test
    void acceptsMatchingSnapshot(@TempDir Path tempDir) throws IOException {
        Path snapshot = tempDir.resolve("docs/openapi.json");
        String document = "{\n  \"openapi\" : \"3.1.0\"\n}\n";
        OpenApiSnapshot.writeOrVerify(snapshot, document, true);

        OpenApiSnapshot.writeOrVerify(snapshot, document, false);
    }

    @Test
    void normalizesEquivalentJson() throws IOException {
        assertThat(OpenApiSnapshot.normalize(mapper, "{\"b\":2,\"a\":1}"))
                .isEqualTo("{\n  \"b\" : 2,\n  \"a\" : 1\n}\n");
    }
}
