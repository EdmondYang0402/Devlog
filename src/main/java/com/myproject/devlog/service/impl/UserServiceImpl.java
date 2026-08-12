package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.ChangePasswordDTO;
import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.dto.UserLoginDTO;
import com.myproject.devlog.pojo.dto.UserRegisterDTO;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.UserInfoVO;
import com.myproject.devlog.pojo.vo.UserLoginVO;
import com.myproject.devlog.service.LoginSessionService;
import com.myproject.devlog.service.UserService;
import com.myproject.devlog.utils.JwtUtil;
import com.myproject.devlog.utils.UserConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.myproject.devlog.common.RoleConstant.USER;
import static com.myproject.devlog.common.StatusConstant.NORMAL;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final LoginSessionService loginSessionService;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, UserConverter userConverter,
                           LoginSessionService loginSessionService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
        this.loginSessionService = loginSessionService;
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        if (Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "账号已被封禁");
        }

        String sessionId = loginSessionService.createSession(user.getId());
        String token;
        try {
            token = JwtUtil.generateToken(user.getId(), sessionId);
        } catch (RuntimeException exception) {
            loginSessionService.deleteSession(sessionId);
            throw exception;
        }
        return new UserLoginVO(user.getId(), token);
    }

    @Override
    public void register(UserRegisterDTO dto) {
        String username = dto.getUsername().trim();
        String email = dto.getEmail().trim();
        // DTO 校验原始长度；这里保留规范化后的长度校验，防止首尾空格掩盖过短用户名。
        if (username.length() < 5 || username.length() > 16) {
            throw new BusinessException("用户名长度必须为 5~16 位");
        }
        log.info("Register service started: username={}, email={}", username, email);

        if (userMapper.selectByUsername(username) != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
        }
        if (userMapper.selectByEmail(email) != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(email);
        user.setRole(USER);
        user.setStatus(NORMAL);
        try {
            if (userMapper.insert(user) != 1) {
                throw new IllegalStateException("用户注册未影响预期记录数");
            }
        } catch (DataIntegrityViolationException exception) {
            // 先查询可提供明确提示；数据库唯一约束仍负责兜住并发注册竞争。
            throw new BusinessException(HttpStatus.CONFLICT, "用户名或邮箱已存在");
        }
        log.info("Register mapper insert succeeded: username={}, email={}", username, email);
    }

    @Override
    public UserInfoVO getInfo(Long id) {
        if (id == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return userConverter.toUserInfoVO(user);
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        return getInfo(userId);
    }


    @Override
    public UserInfoVO updateProfile(UpdateUserDTO dto) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        dto.setNickname(dto.getNickname().trim());
        User update = userConverter.fromProfileUpdateDTO(dto);
        update.setId(userId);
        if (userMapper.updateById(update) != 1) {
            throw new IllegalStateException("用户资料更新未影响预期记录数");
        }
        return getInfo(userId);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "旧密码错误");
        }
        User update = new User();
        update.setId(user.getId());
        update.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        if (userMapper.updateById(update) != 1) {
            throw new IllegalStateException("密码更新未影响预期记录数");
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty() || !JwtUtil.validateToken(token)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Token非法或已过期");
        }

        String sessionId = JwtUtil.parseSessionId(token);
        if (sessionId == null || sessionId.isBlank()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }

        loginSessionService.deleteSession(sessionId);
        UserContext.clear();
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        if (userId == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户状态不合法");
        }
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        if (userMapper.updateById(user) != 1) {
            throw new IllegalStateException("用户状态更新未影响预期记录数");
        }
    }
}
