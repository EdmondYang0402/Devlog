package com.myproject.devlog.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class UploadPropertiesTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class)
            .withPropertyValues(
                    "app.upload.dir=/app/uploads",
                    "app.upload.public-url-prefix=/uploads");

    @Test
    void bindsUploadProperties() {
        contextRunner.run(context -> {
            UploadProperties properties = context.getBean(UploadProperties.class);
            assertThat(properties.getDir()).isEqualTo(Path.of("/app/uploads"));
            assertThat(properties.getPublicUrlPrefix()).isEqualTo("/uploads");
        });
    }

    @EnableConfigurationProperties(UploadProperties.class)
    static class TestConfiguration {
    }
}
