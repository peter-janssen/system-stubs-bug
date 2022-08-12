package com.example;

import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@SpringBootTest(classes = ClassUnderTest.class)
@ExtendWith(SystemStubsExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class SystemStubsEnvBug {

    @Autowired
    private ClassUnderTest classUnderTest;

    @SystemStub
    private static EnvironmentVariables environmentVariables;

    @InjectSoftAssertions
    private SoftAssertions softly;
    private static final String KEY = "TestKey";

    @BeforeAll
    static void initAll() {
        environmentVariables.set(KEY, "value");
    }

    @Test
    @SneakyThrows
    void test() {
        environmentVariables.execute(() -> {
            softly.assertThat(System.getenv().containsKey(KEY)).withFailMessage("Environment variable should be set inside execute method").isTrue();
            softly.assertThat(classUnderTest.getEnvironmentProperties().containsKey(KEY)).withFailMessage("Environment variable should be retrieved from spring").isTrue();
        });
        softly.assertThat(System.getenv().containsKey(KEY)).withFailMessage("Environment variable should be cleared after execute method").isFalse();
        environmentVariables.teardown();
        softly.assertThat(System.getenv().containsKey(KEY)).withFailMessage("Environment variable should definitely be cleared after teardown").isFalse();
    }

}