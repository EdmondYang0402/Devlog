export const ARTICLE_STATUS = Object.freeze({
  DRAFT: 0,
  PUBLISHED: 1
})

export const ARTICLE_STATUS_OPTIONS = Object.freeze([
  { label: '草稿', value: ARTICLE_STATUS.DRAFT },
  { label: '已发布', value: ARTICLE_STATUS.PUBLISHED }
])

export function getArticleStatusLabel(status) {
  return Number(status) === ARTICLE_STATUS.PUBLISHED ? '已发布' : '草稿'
}

export function isArticlePublished(status) {
  return Number(status) === ARTICLE_STATUS.PUBLISHED
}
