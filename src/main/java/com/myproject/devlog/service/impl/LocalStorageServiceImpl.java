package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.config.UploadProperties;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.ImageUploadVO;
import com.myproject.devlog.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.myproject.devlog.utils.PermissionUtil.isAdmin;

@Service
public class LocalStorageServiceImpl implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(LocalStorageServiceImpl.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_AVATAR_FILE_SIZE = 2 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "gif"
    );
    private static final Set<String> ALLOWED_AVATAR_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final Set<String> ALLOWED_AVATAR_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp"
    );

    private final UserMapper userMapper;
    private final UploadProperties uploadProperties;
    private Path uploadRoot;

    public LocalStorageServiceImpl(UserMapper userMapper, UploadProperties uploadProperties) {
        this.userMapper = userMapper;
        this.uploadProperties = uploadProperties;
    }

    @PostConstruct
    public void initializeUploadDirectory() {
        uploadRoot = uploadProperties.getDir().toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
            if (!Files.isDirectory(uploadRoot) || !Files.isWritable(uploadRoot)) {
                throw new IllegalStateException("上传目录不可写: " + uploadRoot);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("无法创建上传目录: " + uploadRoot, exception);
        }
    }

    @Override
    public ImageUploadVO uploadImage(MultipartFile file) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        if (!isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无管理员上传权限");
        }
        return store(file, MAX_FILE_SIZE, ALLOWED_CONTENT_TYPES,
                ALLOWED_EXTENSIONS, "article/cover");
    }

    @Override
    public ImageUploadVO uploadAvatar(MultipartFile file) {
        return store(file, MAX_AVATAR_FILE_SIZE, ALLOWED_AVATAR_CONTENT_TYPES,
                ALLOWED_AVATAR_EXTENSIONS, "user/avatar");
    }

    private ImageUploadVO store(MultipartFile file, long maxFileSize,
                                Set<String> allowedContentTypes,
                                Set<String> allowedExtensions,
                                String relativeDirectory) {
        validateImage(file, maxFileSize, allowedContentTypes, allowedExtensions);

        String extension = getExtension(file);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String objectKey = relativeDirectory + "/" + datePath + "/"
                + UUID.randomUUID() + "." + extension;
        Path target = uploadRoot.resolve(objectKey).normalize();

        if (!target.startsWith(uploadRoot)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "图片保存路径不合法");
        }

        log.info(
                "Saving image: uploadDir={}, targetPath={}",
                uploadRoot,
                target
        );

        try {
            Files.createDirectories(target.getParent());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (AccessDeniedException exception) {
            log.error(
                    "Image upload failed: filename={}, uploadDir={}",
                    file.getOriginalFilename(),
                    uploadRoot,
                    exception
            );
            throw new IllegalStateException("上传目录不可写", exception);
        } catch (IOException exception) {
            log.error(
                    "Image upload failed: filename={}, uploadDir={}",
                    file.getOriginalFilename(),
                    uploadRoot,
                    exception
            );
            throw new IllegalStateException("图片保存失败", exception);
        }

        return new ImageUploadVO(buildPublicUrl(objectKey), objectKey);
    }

    private void validateImage(MultipartFile file, long maxFileSize,
                               Set<String> allowedContentTypes,
                               Set<String> allowedExtensions) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传图片不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(
                    "图片大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null
                || !allowedContentTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "不支持的图片类型");
        }

        if (!allowedExtensions.contains(getExtension(file))) {
            throw new BusinessException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "不支持的图片扩展名");
        }
    }

    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("图片文件名不合法");
        }

        String extension = originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);
        if (extension.isBlank()) {
            throw new BusinessException("图片文件扩展名不能为空");
        }
        return extension;
    }

    private String buildPublicUrl(String objectKey) {
        String prefix = uploadProperties.getPublicUrlPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = "/uploads";
        }
        prefix = "/" + prefix.replaceAll("^/+|/+$", "");
        return prefix + "/" + objectKey.replace('\\', '/');
    }
}
