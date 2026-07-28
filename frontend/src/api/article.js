// src/api/article.js
import request from '@/utils/request'
/* ========================================================================
   一、 通用查询类接口 (对应后端 ArticleController -> /article)
   ======================================================================== */

/**
 * 1. 获取文章分页列表
 * 后端路径：GET /article/list
 * @param {Object} params - 前台文章筛选参数（例如：page, size, categoryId, categorySlug 等）
 */
export function articleListService(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params,
    paramsSerializer: { indexes: null }
  })
}

/**
 * 2. 获取单篇文章详情
 * 后端路径：GET /article/detail/{id}
 * @param {Long|String} id - 文章主键 ID
 */
export function articleDetailService(id) {
  return request({
    url: `/article/${id}`, // 对应后端的 @PathVariable
    method: 'get'
  })
}

/**
 * 3. 增加文章浏览量
 * 后端路径：POST /article/{id}/view
 * @param {Long|String} id - 文章主键 ID
 */
export function increaseArticleViewService(id) {
  return request({
    url: `/article/${id}/view`, // 对应后端的 @PathVariable
    method: 'post'
  })
}

// 前台文章列表：page 从 1 开始，category 可不传
export const getArticleList = (page, size, categoryId, filters = {}) => {
    return request.get('/article/list', {
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
   二、 后台高权管理接口 (对应后端 AdminArticleController -> /admin/article)
   ======================================================================== */

/**
 * 4. 发布新文章
 * 后端路径：POST /admin/article
 * @param {Object} data - 对应后端的 ArticleCreateDTO（JSON 实体）
 */
export function articlePublishService(data) {
  return request({
    url: '/admin/article',
    method: 'post',
    data // 发送 JSON 数据（对应后端的 @RequestBody）
  })
}

/**
 * 5. 修改/更新文章
 * 后端路径：PUT /admin/article
 * @param {Object} data - 对应后端的 ArticleUpdateDTO（包含 id 和修改后的内容）
 */
export function articleUpdateService(data) {
  return request({
    url: '/admin/article',
    method: 'put',
    data // 发送 JSON 数据（对应后端的 @RequestBody）
  })
}

/**
 * 6. 删除文章
 * 后端路径：DELETE /admin/article/{id}
 * @param {Long|String} id - 要删除的文章 ID
 */
export const deleteAdminArticle = (id) => {
    return request.delete(`/admin/article/${id}`)
}

/**
 * 获取文章分页列表 (严格匹配你的 AdminArticleController)
 * 后端路径：GET /admin/article
 */
export function getAdminArticlePage(params) {
  return request({
    url: '/admin/article',
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
    url: `/admin/article/${id}`,
    method: 'get'
  })
}


