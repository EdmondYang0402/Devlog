import request from '@/utils/request.js'

export const commentListService = (articleId) => {
  return request.get(`/comment/article/${articleId}`)
}

export const commentCreateService = (data) => {
  return request.post('/comment', data)
}

export const commentDeleteService = (id) => {
  return request.delete(`/comment/${id}`)
}
