package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.vo.UserInfoVO;
import com.myproject.devlog.service.UserService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerProfileTests {
    @Test
    void ignoresSubmittedUserIdAndReturnsCurrentUsersLatestProfile() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);
        UpdateUserDTO request = new UpdateUserDTO();
        request.setId(999L);
        request.setAvatar("https://example.test/user/avatar/new.webp");

        UserInfoVO latest = UserInfoVO.builder()
                .id(7L)
                .username("current-user")
                .avatar(request.getAvatar())
                .build();

        UserContext.set(7L);
        try {
            when(userService.updateProfile(request)).thenReturn(latest);

            Result<UserInfoVO> result = controller.updateProfile(request);

            assertThat(request.getId()).isEqualTo(7L);
            assertThat(result.getData()).isSameAs(latest);
            verify(userService).updateProfile(request);
        } finally {
            UserContext.clear();
        }
    }
}
