// src/api/article.js
import request from '@/utils/request'
/* ========================================================================
   一、 通用查询类接口 (对应后端 ArticleController -> /articles)
   ======================================================================== */

/**
 * 1. 获取文章分页列表
 * 后端路径：GET /articles
 * @param {Object} params - 前台文章筛选参数（例如：page, size, categoryId, categorySlug 等）
 */
export function articleListService(params) {
  return request({
    url: '/articles',
    method: 'get',
    params,
    paramsSerializer: { indexes: null }
  })
}

/**
 * 2. 获取单篇文章详情
 * 后端路径：GET /articles/{id}
 * @param {Long|String} id - 文章主键 ID
 */
export function articleDetailService(id) {
  return request({
    url: `/articles/${id}`, // 对应后端的 @PathVariable
    method: 'get'
  })
}

/**
 * 3. 增加文章浏览量
 * 后端路径：POST /articles/{id}/view
 * @param {Long|String} id - 文章主键 ID
 */
export function increaseArticleViewService(id) {
  return request({
    url: `/articles/${id}/view`, // 对应后端的 @PathVariable
    method: 'post'
  })
}

// 前台文章列表：page 从 1 开始，category 可不传
export const getArticleList = (page, size, categoryId, filters = {}) => {
    return request.get('/articles', {
        params: {
          page,
          size,
          categoryId: categoryId ?? undefined,
          keyword: filters.keyword || undefined,
          tagIds: Array.isArray(filters.tagIds) && filters.tagIds.length ? filters.tagIds : undefined
        },
        paramsSerializer: { indexes: null }
    })
}


/* ========================================================================
   二、 后台高权管理接口 (对应后端 AdminArticleController -> /admin/articles)
   ======================================================================== */

/**
 * 4. 发布新文章
 * 后端路径：POST /admin/articles
 * @param {Object} data - 对应后端的 ArticleCreateDTO（JSON 实体）
 */
export function articlePublishService(data) {
  return request({
    url: '/admin/articles',
    method: 'post',
    data // 发送 JSON 数据（对应后端的 @RequestBody）
  })
}

/**
 * 5. 修改/更新文章
 * 后端路径：PUT /admin/articles
 * @param {Object} data - 对应后端的 ArticleUpdateDTO（包含 id 和修改后的内容）
 */
export function articleUpdateService(data) {
  return request({
    url: '/admin/articles',
    method: 'put',
    data // 发送 JSON 数据（对应后端的 @RequestBody）
  })
}

/**
 * 6. 删除文章
 * 后端路径：DELETE /admin/articles/{id}
 * @param {Long|String} id - 要删除的文章 ID
 */
export const deleteAdminArticle = (id) => {
    return request.delete(`/admin/articles/${id}`)
}

/**
 * 获取文章分页列表 (严格匹配你的 AdminArticleController)
 * 后端路径：GET /admin/articles
 */
export function getAdminArticlePage(params) {
  return request({
    url: '/admin/articles',
    method: 'get',
    params: {
      page: params.page || 1,
      size: params.size || 10,
      title: params.title || undefined,
      status: params.status === '' ? undefined : params.status
    }
  })
}

export function getAdminArticleDetail(id) {
  return request({
    url: `/admin/articles/${id}`,
    method: 'get'
  })
}


