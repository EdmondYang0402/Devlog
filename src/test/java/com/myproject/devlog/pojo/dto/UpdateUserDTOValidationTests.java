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
        dto.setNickname("用户昵称");
        dto.setAvatar("");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void rejectsAnAvatarUrlLongerThanFiveHundredCharacters() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNickname("用户昵称");
        dto.setAvatar("a".repeat(501));

        assertThat(validator.validate(dto))
                .anyMatch(violation -> "头像URL不能超过500个字符".equals(violation.getMessage()));
    }

    @Test
    void rejectsBlankOrOverlongNickname() {
        UpdateUserDTO blank = new UpdateUserDTO();
        blank.setNickname("   ");
        UpdateUserDTO overlong = new UpdateUserDTO();
        overlong.setNickname("昵".repeat(31));

        assertThat(validator.validate(blank))
                .anyMatch(violation -> "昵称不能为空".equals(violation.getMessage()));
        assertThat(validator.validate(overlong))
                .anyMatch(violation -> "昵称不能超过30个字符".equals(violation.getMessage()));
    }
}
