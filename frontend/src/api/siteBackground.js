import request from '@/utils/request.js'

// 后台接口受现有管理员鉴权保护，JWT 由统一 Axios 请求拦截器自动附加。
export const getAdminBackgroundPage = params => request.get('/admin/site-backgrounds', { params })
export const getAdminBackgroundDetail = id => request.get(`/admin/site-backgrounds/${id}`)
export const createBackground = data => request.post('/admin/site-backgrounds', data)
export const updateBackground = (id, data) => request.put(`/admin/site-backgrounds/${id}`, data)
export const deleteBackground = id => request.delete(`/admin/site-backgrounds/${id}`)

// 公开接口供前台访客读取已启用背景，不要求登录，也不在组件中重复创建请求实例。
export const getPublicBackgrounds = () => request.get('/site/backgrounds')
