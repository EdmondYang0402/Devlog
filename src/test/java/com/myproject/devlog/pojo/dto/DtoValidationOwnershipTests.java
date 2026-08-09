package com.myproject.devlog.pojo.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoValidationOwnershipTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void projectStructureRulesBelongToDto() {
        ProjectCreateDTO dto = validProject();
        assertTrue(validator.validate(dto).isEmpty());

        dto.setName("   ");
        assertFalse(validator.validate(dto).isEmpty());
        dto = validProject();
        dto.setSummary("a".repeat(301));
        assertFalse(validator.validate(dto).isEmpty());
        dto = validProject();
        dto.setStatus(5);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validProject();
        dto.setFeatured(2);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validProject();
        dto.setSortOrder(100001);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validProject();
        dto.setTechStack(new ArrayList<>(java.util.Collections.nCopies(21, "Java")));
        assertFalse(validator.validate(dto).isEmpty());
        dto = validProject();
        dto.setTechStack(List.of("a".repeat(51)));
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void mediaReviewStructureRulesBelongToDto() {
        MediaReviewCreateDTO dto = validMediaReview();
        assertTrue(validator.validate(dto).isEmpty());

        dto.setTitle("   ");
        assertFalse(validator.validate(dto).isEmpty());
        dto = validMediaReview();
        dto.setTitle("a".repeat(201));
        assertFalse(validator.validate(dto).isEmpty());
        dto = validMediaReview();
        dto.setMediaType(4);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validMediaReview();
        dto.setStatus(-1);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validMediaReview();
        dto.setRating(11);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validMediaReview();
        dto.setShortReview("a".repeat(501));
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void siteBackgroundStructureRulesBelongToDto() {
        SiteBackgroundCreateDTO dto = validBackground();
        assertTrue(validator.validate(dto).isEmpty());

        dto.setImageUrl("   ");
        assertFalse(validator.validate(dto).isEmpty());
        dto = validBackground();
        dto.setImageUrl("a".repeat(501));
        assertFalse(validator.validate(dto).isEmpty());
        dto = validBackground();
        dto.setTitle("a".repeat(101));
        assertFalse(validator.validate(dto).isEmpty());
        dto = validBackground();
        dto.setEnabled(2);
        assertFalse(validator.validate(dto).isEmpty());
        dto = validBackground();
        dto.setSortOrder(-100001);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void userCredentialStructureRulesBelongToDto() {
        UserLoginDTO login = new UserLoginDTO();
        login.setUsername("alice");
        login.setPassword("   ");
        assertFalse(validator.validate(login).isEmpty());

        UserRegisterDTO registration = new UserRegisterDTO();
        registration.setUsername("alice");
        registration.setPassword("secret");
        registration.setEmail("   ");
        assertFalse(validator.validate(registration).isEmpty());
        registration.setEmail("invalid-email");
        assertFalse(validator.validate(registration).isEmpty());
        registration.setEmail("alice@example.com");
        registration.setPassword("1234");
        assertFalse(validator.validate(registration).isEmpty());
    }

    @Test
    void siteKeywordElementRulesBelongToDto() {
        SiteConfigUpdateDTO dto = new SiteConfigUpdateDTO();
        dto.setSiteTitle("DevLog");
        dto.setAuthorName("作者");
        dto.setHeroKeywords(List.of("Java", "Spring"));
        assertTrue(validator.validate(dto).isEmpty());

        dto.setHeroKeywords(List.of("   "));
        assertFalse(validator.validate(dto).isEmpty());
        dto.setHeroKeywords(List.of("a".repeat(31)));
        assertFalse(validator.validate(dto).isEmpty());
        dto.setHeroKeywords(java.util.Collections.nCopies(9, "Java"));
        assertFalse(validator.validate(dto).isEmpty());
    }

    private ProjectCreateDTO validProject() {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setName("DevLog");
        dto.setSummary("项目简介");
        dto.setStatus(1);
        return dto;
    }

    private MediaReviewCreateDTO validMediaReview() {
        MediaReviewCreateDTO dto = new MediaReviewCreateDTO();
        dto.setTitle("作品");
        dto.setMediaType(2);
        dto.setStatus(2);
        dto.setRating(9);
        return dto;
    }

    private SiteBackgroundCreateDTO validBackground() {
        SiteBackgroundCreateDTO dto = new SiteBackgroundCreateDTO();
        dto.setImageUrl("https://cdn.example.com/background.webp");
        dto.setEnabled(1);
        dto.setSortOrder(0);
        return dto;
    }
}
