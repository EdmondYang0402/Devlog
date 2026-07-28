package com.myproject.devlog.pojo.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateUserDTOValidationTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsAnEmptyAvatarToUseTheFallback() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setAvatar("");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void rejectsAnAvatarUrlLongerThanFiveHundredCharacters() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setAvatar("a".repeat(501));

        assertThat(validator.validate(dto))
                .anyMatch(violation -> "头像URL不能超过500个字符".equals(violation.getMessage()));
    }
}
