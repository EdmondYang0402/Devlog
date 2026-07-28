package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SiteBackgroundValidatorTests {
    private final SiteBackgroundValidator validator = new SiteBackgroundValidator();

    @Test
    void imageUrlIsRequiredAndMustUseHttpScheme() {
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl(null));
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl("   "));
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl("ftp://cdn.example.com/a.jpg"));
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl("/images/a.jpg"));
        assertEquals("/uploads/site/background/a.jpg",
                validator.normalizeImageUrl("/uploads/site/background/a.jpg"));
        assertEquals("https://cdn.example.com/a.jpg",
                validator.normalizeImageUrl(" https://cdn.example.com/a.jpg "));
    }

    @Test
    void imageUrlRejectsValuesOverDatabaseLength() {
        String url = "https://example.com/" + "a".repeat(482);
        assertThrows(BusinessException.class, () -> validator.normalizeImageUrl(url));
    }

    @Test
    void titleIsOptionalAndHonorsHundredCharacterBoundary() {
        assertNull(validator.normalizeTitle(null));
        assertNull(validator.normalizeTitle("   "));
        assertEquals("a".repeat(100), validator.normalizeTitle("a".repeat(100)));
        assertThrows(BusinessException.class, () -> validator.normalizeTitle("a".repeat(101)));
    }

    @Test
    void enabledDefaultsToOneAndOnlyAcceptsBinaryValues() {
        assertEquals(1, validator.normalizeEnabled(null));
        assertEquals(0, validator.normalizeEnabled(0));
        assertEquals(1, validator.normalizeEnabled(1));
        BusinessException exception = assertThrows(BusinessException.class,
                () -> validator.normalizeEnabled(2));
        assertEquals("启用状态不合法", exception.getMessage());
    }

    @Test
    void sortOrderUsesDefaultAndAllowsInclusiveBoundaries() {
        assertEquals(0, validator.normalizeSortOrder(null));
        assertEquals(-100000, validator.normalizeSortOrder(-100000));
        assertEquals(100000, validator.normalizeSortOrder(100000));
        assertThrows(BusinessException.class, () -> validator.normalizeSortOrder(-100001));
        assertThrows(BusinessException.class, () -> validator.normalizeSortOrder(100001));
    }
}
