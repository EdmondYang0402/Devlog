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
        if (dto.getUsername() == null || dto.getPassword() == null) {
            throw new RuntimeException("账户名和密码不能为空");
        }

        User user = userMapper.getByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户尚未注册");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
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
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new BusinessException("用户名不能为空");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BusinessException("邮箱不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }

        String username = dto.getUsername().trim();
        String email = dto.getEmail().trim();
        if (username.length() < 5 || username.length() > 16) {
            throw new BusinessException("用户名长度必须为 5~16 位");
        }
        if (dto.getPassword().length() < 5 || dto.getPassword().length() > 16) {
            throw new BusinessException("密码长度必须为 5~16 位");
        }
        log.info("Register service started: username={}, email={}", username, email);

        if (userMapper.getByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.getByEmail(email) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(email);
        user.setRole(USER);
        user.setStatus(NORMAL);
        try {
            userMapper.insert(user);
        } catch (DataIntegrityViolationException exception) {
            // 先查询可提供明确提示；数据库唯一约束仍负责兜住并发注册竞争。
            throw new BusinessException("用户名或邮箱已存在");
        }
        log.info("Register mapper insert succeeded: username={}, email={}", username, email);
    }

    @Override
    public UserInfoVO getInfo(Long id) {
        return userConverter.toUserInfoVO(userMapper.getById(id));
    }


    @Override
    public UserInfoVO updateProfile(UpdateUserDTO dto) {
        userMapper.update(userConverter.toUser(dto));
        return getInfo(dto.getId());
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        User user = userMapper.getById(UserContext.get());
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        User update = new User();
        update.setId(user.getId());
        update.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.update(update);
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
        return userMapper.getByUsername(username);
    }

    @Override
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        userMapper.update(user);
    }
}
