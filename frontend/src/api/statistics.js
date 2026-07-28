import request from '@/utils/request.js'

export const profileStatisticsService = () => {
  return request.get('/statistics/profile')
}
