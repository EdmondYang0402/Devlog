import request from '@/utils/request'

export const getAdminTags = () => request.get('/admin/tags')
export const getAdminTagOptions = () => request.get('/admin/tags/options')
export const getPublicTags = () => request.get('/tags')
export const createTag = data => request.post('/admin/tags', data)
export const updateTag = (id, data) => request.put(`/admin/tags/${id}`, data)
export const deleteTag = id => request.delete(`/admin/tags/${id}`)
