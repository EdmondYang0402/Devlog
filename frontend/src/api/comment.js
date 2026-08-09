import request from '@/utils/request.js'

export const commentListService = (articleId) => {
  return request.get(`/comments/articles/${articleId}`)
}

export const commentCreateService = (data) => {
  return request.post('/comments', data)
}

export const commentDeleteService = (id) => {
  return request.delete(`/comments/${id}`)
}
