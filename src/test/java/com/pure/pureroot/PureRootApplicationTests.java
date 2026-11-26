package com.pure.pureroot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "logging.file.path=target/test-logs")
class PureRootApplicationTests {

    @Test
    void contextLoads() {
    }

}
