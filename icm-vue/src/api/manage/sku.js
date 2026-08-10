import request from '@/utils/request'

// 查询商品列表
export function listSku(query) {
  return request({ url: '/manage/sku/list', method: 'get', params: query })
}

// 查询商品详细
export function getSku(skuId) {
  return request({ url: '/manage/sku/' + skuId, method: 'get' })
}

// 新增商品
export function addSku(data) {
  return request({ url: '/manage/sku', method: 'post', data: data })
}

// 修改商品
export function updateSku(data) {
  return request({ url: '/manage/sku', method: 'put', data: data })
}

// 删除商品
export function delSku(skuIds) {
  return request({ url: '/manage/sku/' + skuIds, method: 'delete' })
}

// 导出商品
export function exportSku(data) {
  return request({ url: '/manage/sku/export', method: 'post', data: data, responseType: 'blob' })
}

// 导入商品
export function importSku(data) {
  return request({ url: '/manage/sku/import', method: 'post', data: data })
}
