package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectValidatorTests {
    private final ProjectValidator validator = new ProjectValidator();

    @Test
    void nameRulesCoverNullBlankAndBoundaries() {
        assertThrows(BusinessException.class, () -> validator.normalizeName(null));
        assertThrows(BusinessException.class, () -> validator.normalizeName("   "));
        assertEquals("a".repeat(100), validator.normalizeName("a".repeat(100)));
        assertThrows(BusinessException.class, () -> validator.normalizeName("a".repeat(101)));
    }

    @Test
    void summaryRulesCoverBlankAndBoundaries() {
        assertThrows(BusinessException.class, () -> validator.normalizeSummary(" "));
        assertEquals("a".repeat(300), validator.normalizeSummary("a".repeat(300)));
        assertThrows(BusinessException.class, () -> validator.normalizeSummary("a".repeat(301)));
    }

    @Test
    void statusAllowsOnlyZeroThroughFourAndRejectsNull() {
        assertThrows(BusinessException.class, () -> validator.validateStatus(null));
        assertThrows(BusinessException.class, () -> validator.validateStatus(-1));
        assertDoesNotThrow(() -> validator.validateStatus(0));
        assertDoesNotThrow(() -> validator.validateStatus(4));
        assertThrows(BusinessException.class, () -> validator.validateStatus(5));
    }

    @Test
    void featuredDefaultsToZeroAndAllowsOnlyBinaryValues() {
        assertEquals(0, validator.normalizeFeatured(null));
        assertEquals(0, validator.normalizeFeatured(0));
        assertEquals(1, validator.normalizeFeatured(1));
        assertThrows(BusinessException.class, () -> validator.normalizeFeatured(2));
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
    void techStackRejectsTooManyItemsAndOverlongItem() {
        List<String> tooMany = new ArrayList<>();
        for (int index = 0; index < 21; index++) {
            tooMany.add("tech-" + index);
        }
        assertThrows(BusinessException.class, () -> validator.normalizeTechStack(tooMany));
        assertThrows(BusinessException.class,
                () -> validator.normalizeTechStack(List.of("a".repeat(51))));
    }
}
