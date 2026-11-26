package com.pure.pureroot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "logging.file.path=target/test-logs")
class EchoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /echo returns the value as plain text")
    void echo_returnsValue() throws Exception {
        mockMvc.perform(post("/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("name", "hello world")))
                .andExpect(status().isOk())
                .andExpect(content().string("hello world"));
    }

    @Test
    @DisplayName("POST /echo returns the value unchanged including quotes and newlines")
    void echo_returnsValueWithSpecialCharacters() throws Exception {
        String value = "中文 \"quoted\" line1\nline2 {json:true}";

        mockMvc.perform(post("/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("text", value)))
                .andExpect(status().isOk())
                .andExpect(content().string(value));
    }

    @Test
    @DisplayName("POST /echo returns an empty string when value is missing")
    void echo_missingValue_returnsEmptyString() throws Exception {
        mockMvc.perform(post("/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"name\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("POST /echo logs the value at INFO level")
    void echo_logsValue() throws Exception {
        mockMvc.perform(post("/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("log", "log-me-123")))
                .andExpect(status().isOk());

        assertThat(readInfoLog()).anyMatch(line -> line.contains("log-me-123"));
    }

    private List<String> readInfoLog() throws IOException {
        Path logFile = Path.of("target/test-logs/pure-root/8080/info/info.log");
        return Files.exists(logFile)
                ? Files.readAllLines(logFile, StandardCharsets.UTF_8)
                : List.of();
    }

    private String body(String key, String value) {
        JSONObject json = JSONUtil.createObj().set("key", key).set("value", value);
        return json.toString();
    }
}
