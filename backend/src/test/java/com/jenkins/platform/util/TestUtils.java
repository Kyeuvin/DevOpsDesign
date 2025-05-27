package com.jenkins.platform.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String readResource(String path) throws IOException {
        return Files.readString(new ClassPathResource(path).getFile().toPath());
    }

    public static <T> T readResourceAsObject(String path, Class<T> valueType) throws IOException {
        String content = readResource(path);
        return objectMapper.readValue(content, valueType);
    }
}