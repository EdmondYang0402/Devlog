package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MediaReviewValidatorTests {
    private final MediaReviewValidator validator = new MediaReviewValidator();

    @Test
    void titleIsNormalizedAfterDtoValidation() {
        assertEquals("作品", validator.normalizeTitle("  作品  "));
    }

    @Test
    void mediaTypeAndStatusRejectValuesOutsideDefinedRange() {
        assertDoesNotThrow(() -> validator.validateMediaType(0));
        assertDoesNotThrow(() -> validator.validateMediaType(3));
        assertThrows(BusinessException.class, () -> validator.validateMediaType(4));
        assertDoesNotThrow(() -> validator.validateStatus(0));
        assertDoesNotThrow(() -> validator.validateStatus(3));
        assertThrows(BusinessException.class, () -> validator.validateStatus(-1));
    }

    @Test
    void optionalTextIsNormalized() {
        assertNull(validator.normalizeShortReview("   "));
        assertEquals("短评", validator.normalizeShortReview("  短评  "));
    }

    @Test
    void coverMustBeAWellFormedHttpOrHttpsUrl() {
        assertEquals("https://example.com/cover.jpg",
                validator.normalizeCoverUrl(" https://example.com/cover.jpg "));
        assertThrows(BusinessException.class,
                () -> validator.normalizeCoverUrl("ftp://example.com/cover.jpg"));
        assertThrows(BusinessException.class,
                () -> validator.normalizeCoverUrl("https://"));
        assertEquals("/uploads/article/cover/2026/07/cover.webp",
                validator.normalizeCoverUrl("/uploads/article/cover/2026/07/cover.webp"));
    }
}
