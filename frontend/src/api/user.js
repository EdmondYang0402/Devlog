import request from '@/utils/request.js'

// 登录
export const userLoginService = (data) => {
    return request.post('/users/login', data)
}

// 注册
export const userRegisterService = (data) => {
    return request.post('/users/register', data)
}

// 获取用户信息
export const userInfoService = () => request.get('/users/me')

export const userLogoutService = () => request.post('/users/logout')

// 修改个人资料
export const userUpdateProfileService = (data) => request.put('/users/profile', data)

export const userAvatarUploadService = file => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/users/avatar/upload', formData)
}

// 修改密码
export const userUpdatePasswordService = (data) => request.patch('/users/password', data)

//退出登錄
