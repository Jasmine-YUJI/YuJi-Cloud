import request from '@/utils/request'

// 查询栏目管理列表
export function listCatalog(query) {
  return request({
    url: '/cms/catalog/list',
    method: 'get',
    params: query
  })
}

// 查询栏目管理详细
export function getCatalog(catalogId) {
  return request({
    url: '/cms/catalog/getInfo/' + catalogId,
    method: 'get'
  })
}

// 新增栏目管理
export function addCatalog(data) {
  return request({
    url: '/cms/catalog',
    method: 'post',
    data: data
  })
}

// 修改栏目管理
export function updateCatalog(data) {
  return request({
    url: '/cms/catalog',
    method: 'put',
    data: data
  })
}

// 删除栏目管理
export function delCatalog(catalogId) {
  return request({
    url: '/cms/catalog/' + catalogId,
    method: 'delete'
  })
}

// 插叙栏目类型---
export function getCatalogTypes() {
  return request({
    url: '/cms/catalog/getCatalogTypes',
    method: 'get',
  })
}

// 查询栏目树结构
export function getCatalogTreeData(params) {
  return request({
    url: '/cms/catalog/treeData',
    method: 'get',
    params: params
  })
}

// 发布栏目
export function publishCatalog(data) {
  return request({
    url: '/cms/catalog/publish',
    method: 'post',
    data: data
  })
}

export function sortCatalog(data) {
  return request({
    url: '/cms/catalog/sort',
    method: 'put',
    data: data
  })
}

// 获取栏目扩展属性
export function getCatalogExtends(params) {
  return request({
    url: '/cms/catalog/extends',
    method: 'get',
    params: params
  })
}

// 保存栏目扩展属性
export function saveCatalogExtends(catalogId, data) {
  return request({
    url: '/cms/catalog/extends/' + catalogId,
    method: 'put',
    data: data
  })
}

// 栏目扩展配置应用到栏目
export function applyConfigPropsToChildren(data) {
  return request({
    url: '/cms/catalog/apply_children/config_props',
    method: 'put',
    data: data
  })
}
