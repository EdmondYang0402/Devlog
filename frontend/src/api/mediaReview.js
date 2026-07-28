import request from '@/utils/request.js'

// 前台接口是公开展示能力；后台接口复用现有管理员鉴权和统一上传体系。
export const getMediaReviewPage = params => request.get('/media-reviews', { params })
export const getMediaReviewDetail = id => request.get(`/media-reviews/${id}`)

export const getAdminMediaReviewPage = params => request.get('/admin/media-reviews', { params })
export const getAdminMediaReviewDetail = id => request.get(`/admin/media-reviews/${id}`)
export const createMediaReview = data => request.post('/admin/media-reviews', data)
export const updateMediaReview = (id, data) => request.put(`/admin/media-reviews/${id}`, data)
export const deleteMediaReview = id => request.delete(`/admin/media-reviews/${id}`)
