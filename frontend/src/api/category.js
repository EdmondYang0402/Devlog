import request from '@/utils/request'

export function getAdminCategoryList() {
  return request.get('/admin/categories')
}

export function getCategoryList() {
  return request.get('/categories')
}

export function getCategoryTags(categoryId) {
  return request.get(`/categories/${categoryId}/tags`)
}

export function createCategory(data) {
  return request.post('/admin/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/admin/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/admin/categories/${id}`)
}

export function getAdminCategoryTags(categoryId) {
  return request.get(`/admin/categories/${categoryId}/tags`)
}

export function updateAdminCategoryTags(categoryId, tagIds) {
  return request.put(`/admin/categories/${categoryId}/tags`, { tagIds })
}
