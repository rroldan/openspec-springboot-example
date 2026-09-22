package com.example.taskmanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class OpenApiSnapshot {

    private OpenApiSnapshot() {
    }

    static String normalize(ObjectMapper mapper, String document) throws IOException {
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("OpenAPI response was empty");
        }
        JsonNode json = mapper.readTree(document);
        if (json == null || !json.isObject() || json.isEmpty()) {
            throw new IllegalArgumentException("OpenAPI response was not a non-empty JSON object");
        }
        ((ObjectNode) json).remove("servers");
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json) + System.lineSeparator();
    }

    static void writeOrVerify(Path snapshot, String normalizedDocument, boolean write) throws IOException {
        if (write) {
            Files.createDirectories(snapshot.getParent());
            Files.writeString(snapshot, normalizedDocument);
            return;
        }

        if (!Files.isRegularFile(snapshot)) {
            throw new AssertionError("OpenAPI snapshot is missing: " + snapshot
                    + ". Generate it with -Dopenapi.snapshot.write=true.");
        }
        String committed = Files.readString(snapshot);
        if (!committed.equals(normalizedDocument)) {
            throw new AssertionError("OpenAPI snapshot is stale: " + snapshot
                    + ". Regenerate it with -Dopenapi.snapshot.write=true and review the diff.");
        }
    }

    static String invalidJsonMessage(ObjectMapper mapper, String document) {
        try {
            normalize(mapper, document);
            return "";
        } catch (JsonProcessingException exception) {
            return "OpenAPI response was not valid JSON";
        } catch (IOException exception) {
            return exception.getMessage();
        }
    }
}
