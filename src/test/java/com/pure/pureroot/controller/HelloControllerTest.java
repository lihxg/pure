package com.pure.pureroot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "logging.file.path=target/test-logs")
class HelloControllerTest {

    private static final Path LOG_DIR = Path.of("target/test-logs/pure-root/8080");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /hello returns hello world")
    void hello_returnsHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello world"));
    }

    @Test
    @DisplayName("info log line uses the pattern with trace id")
    void hello_writesInfoLogWithPattern() throws Exception {
        mockMvc.perform(get("/hello").header("X-Trace-Id", "pattern-check"))
                .andExpect(status().isOk());

        String line = lastLineContaining(readLogLines("info/info.log"), "hi~");

        assertThat(line).contains("[INFO]");
        assertThat(line).contains("[pattern-check] hi~");
        assertThat(line).contains("[" + HelloController.class.getName() + ":");
    }

    @Test
    @DisplayName("trace id from X-Trace-Id header is propagated to the log")
    void hello_usesTraceIdFromHeader() throws Exception {
        mockMvc.perform(get("/hello").header("X-Trace-Id", "trace-from-upstream"))
                .andExpect(status().isOk());

        assertThat(lastLineContaining(readLogLines("info/info.log"), "hi~"))
                .contains("[trace-from-upstream] hi~");
    }

    @Test
    @DisplayName("INFO traffic is not written to warn or error files")
    void infoTraffic_isNotWrittenToWarnOrErrorFiles() throws Exception {
        mockMvc.perform(get("/hello").header("X-Trace-Id", "level-check"))
                .andExpect(status().isOk());

        assertThat(readLogLines("warn/warn.log")).noneMatch(line -> line.contains("level-check"));
        assertThat(readLogLines("error/error.log")).noneMatch(line -> line.contains("level-check"));
    }

    private List<String> readLogLines(String relativePath) throws IOException {
        Path path = LOG_DIR.resolve(relativePath);
        if (!Files.exists(path)) {
            return List.of();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    private String lastLineContaining(List<String> lines, String keyword) {
        return lines.stream()
                .filter(line -> line.contains(keyword))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new AssertionError("no log line containing '" + keyword + "'"));
    }
}
