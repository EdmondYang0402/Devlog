import request from '@/utils/request.js'

export const siteProfileService = () => request.get('/site/profile')
export const adminSiteProfileService = () => request.get('/admin/site/profile')
export const adminSiteProfileUpdateService = data => request.put('/admin/site/profile', data)
export const adminImageUploadService = file => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload/image', formData)
}
