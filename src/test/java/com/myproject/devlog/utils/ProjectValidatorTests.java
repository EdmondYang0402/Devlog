package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectValidatorTests {
    private final ProjectValidator validator = new ProjectValidator();

    @Test
    void requiredTextIsNormalizedAfterDtoValidation() {
        assertEquals("DevLog", validator.normalizeName("  DevLog  "));
        assertEquals("项目简介", validator.normalizeSummary("  项目简介  "));
    }

    @Test
    void optionalNumbersOnlyApplyDefaultsAfterDtoValidation() {
        assertEquals(0, validator.normalizeFeatured(null));
        assertEquals(0, validator.normalizeFeatured(0));
        assertEquals(1, validator.normalizeFeatured(1));
        assertEquals(0, validator.normalizeSortOrder(null));
        assertEquals(12, validator.normalizeSortOrder(12));
    }

    @Test
    void completedDateCannotBeEarlierThanStartedDate() {
        LocalDate started = LocalDate.of(2026, 7, 2);
        assertThrows(BusinessException.class,
                () -> validator.validateDates(started, started.minusDays(1)));
        assertDoesNotThrow(() -> validator.validateDates(started, started));
        assertDoesNotThrow(() -> validator.validateDates(null, started));
    }

    @Test
    void urlsAreOptionalButMustUseHttpOrHttps() {
        assertNull(validator.normalizeCoverUrl("  "));
        assertEquals("https://example.com/project", validator.normalizeGithubUrl(" https://example.com/project "));
        assertEquals("/uploads/article/cover/project.png",
                validator.normalizeCoverUrl("/uploads/article/cover/project.png"));
        assertThrows(BusinessException.class,
                () -> validator.normalizeDemoUrl("ftp://example.com/demo"));
        assertThrows(BusinessException.class,
                () -> validator.normalizeCoverUrl("https://"));
    }

    @Test
    void techStackRemovesBlankValuesAndDeduplicatesInOriginalOrder() {
        List<String> result = validator.normalizeTechStack(
                Arrays.asList(" Java ", "", null, "Spring Boot", "Java", "  "));
        assertEquals(List.of("Java", "Spring Boot"), result);
    }

    @Test
    void serializedTechStackStillHonorsDatabaseCapacity() {
        assertDoesNotThrow(() -> validator.validateSerializedTechStack("a".repeat(1000)));
        assertThrows(BusinessException.class,
                () -> validator.validateSerializedTechStack("a".repeat(1001)));
    }
}
