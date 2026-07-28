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
    void titleRulesCoverNullBlankAndBoundaries() {
        assertThrows(BusinessException.class, () -> validator.normalizeTitle(null));
        assertThrows(BusinessException.class, () -> validator.normalizeTitle("   "));
        assertEquals("a".repeat(200), validator.normalizeTitle("a".repeat(200)));
        assertThrows(BusinessException.class, () -> validator.normalizeTitle("a".repeat(201)));
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
    void ratingAllowsNullAndInclusiveOneToTenOnly() {
        assertNull(validator.validateRating(null));
        assertEquals(1, validator.validateRating(1));
        assertEquals(10, validator.validateRating(10));
        assertThrows(BusinessException.class, () -> validator.validateRating(0));
        assertThrows(BusinessException.class, () -> validator.validateRating(11));
    }

    @Test
    void optionalTextIsNormalizedAndLengthChecked() {
        assertNull(validator.normalizeShortReview("   "));
        assertEquals("短评", validator.normalizeShortReview("  短评  "));
        assertThrows(BusinessException.class,
                () -> validator.normalizeShortReview("a".repeat(501)));
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
