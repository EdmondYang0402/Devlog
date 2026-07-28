export const MEDIA_TYPES = Object.freeze({ BOOK: 0, MOVIE: 1, ANIME: 2, GAME: 3 })
export const MEDIA_STATUS = Object.freeze({ PLANNED: 0, IN_PROGRESS: 1, COMPLETED: 2, DROPPED: 3 })

export const mediaTypeOptions = Object.freeze([
  { value: MEDIA_TYPES.BOOK, key: 'book', icon: 'ti-book' },
  { value: MEDIA_TYPES.MOVIE, key: 'movie', icon: 'ti-movie' },
  { value: MEDIA_TYPES.ANIME, key: 'anime', icon: 'ti-device-tv' },
  { value: MEDIA_TYPES.GAME, key: 'game', icon: 'ti-device-gamepad-2' }
])

export const statusOptions = Object.freeze([
  { value: MEDIA_STATUS.PLANNED, key: 'planned' },
  { value: MEDIA_STATUS.IN_PROGRESS, key: 'inProgress' },
  { value: MEDIA_STATUS.COMPLETED, key: 'completed' },
  { value: MEDIA_STATUS.DROPPED, key: 'dropped' }
])

export const typeKey = mediaType => mediaTypeOptions.find(item => item.value === Number(mediaType))?.key || 'unknown'

export const statusKey = (mediaType, status) => {
  // 数据库状态码统一，但书籍“已读”和电影“已看”等展示文案应随作品类型变化。
  const type = typeKey(mediaType)
  const state = statusOptions.find(item => item.value === Number(status))?.key
  return state ? `media.status.${type}.${state}` : 'media.status.unknown'
}

// 后端保存 1～10 的整数，前端评分组件显示 0.5～5 星，避免向接口提交浮点数。
export const ratingToStars = rating => rating == null ? 0 : Number(rating) / 2
export const starsToRating = stars => stars == null || stars === 0 ? null : Math.round(Number(stars) * 2)
