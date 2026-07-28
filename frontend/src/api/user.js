import request from '@/utils/request.js'

// 登录
export const userLoginService = (data) => {
    return request.post('/user/login', data)
}

// 注册
export const userRegisterService = (data) => {
    return request.post('/user/register', data)
}

// 获取用户信息
export const userInfoService = () => request.get('/user/me')

export const userLogoutService = () => request.post('/user/logout')

// 修改个人资料
export const userUpdateProfileService = (data) => request.put('/user/profile', data)

export const userAvatarUploadService = file => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/user/avatar/upload', formData)
}

// 修改密码
export const userUpdatePasswordService = (data) => request.patch('/user/password', data)

//退出登錄
