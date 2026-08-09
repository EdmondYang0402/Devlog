package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SiteBackgroundValidatorTests {
    private final SiteBackgroundValidator validator = new SiteBackgroundValidator();

    @Test
    void imageUrlMustUseSupportedSchemeAfterDtoValidation() {
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl("ftp://cdn.example.com/a.jpg"));
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl("/images/a.jpg"));
        assertEquals("/uploads/site/background/a.jpg",
                validator.normalizeImageUrl("/uploads/site/background/a.jpg"));
        assertEquals("https://cdn.example.com/a.jpg",
                validator.normalizeImageUrl(" https://cdn.example.com/a.jpg "));
    }

    void titleIsOptionalAndNormalized() {
        assertNull(validator.normalizeTitle(null));
        assertNull(validator.normalizeTitle("   "));
        assertEquals("背景", validator.normalizeTitle("  背景  "));
    }

    @Test
    void optionalNumbersOnlyApplyDefaultsAfterDtoValidation() {
        assertEquals(1, validator.normalizeEnabled(null));
        assertEquals(0, validator.normalizeEnabled(0));
        assertEquals(1, validator.normalizeEnabled(1));
        assertEquals(0, validator.normalizeSortOrder(null));
        assertEquals(-100000, validator.normalizeSortOrder(-100000));
        assertEquals(100000, validator.normalizeSortOrder(100000));
    }
}
