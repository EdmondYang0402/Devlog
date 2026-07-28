import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 8000 })

api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('token')?.trim()
  if (token && token !== 'null' && token !== 'undefined') {
    cfg.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
  } else {
    delete cfg.headers.Authorization
  }
  return cfg
})

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// Posts
// GET /api/posts?page=0&size=10&category=技术
export const getPosts      = (params) => api.get('/posts', { params })
export const getPostById   = (id)     => api.get(`/posts/${id}`)

// Categories
export const getCategories = ()       => api.get('/categories')

// Stats
export const getStats      = ()       => api.get('/stats')

// Auth
export const login         = (data)   => api.post('/auth/login', data)

export default api
