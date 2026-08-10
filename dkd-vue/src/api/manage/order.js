import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({ url: '/manage/order/list', method: 'get', params: query })
}

// 查询订单详细
export function getOrder(id) {
  return request({ url: '/manage/order/' + id, method: 'get' })
}

// 新增订单
export function addOrder(data) {
  return request({ url: '/manage/order', method: 'post', data: data })
}

// 修改订单
export function updateOrder(data) {
  return request({ url: '/manage/order', method: 'put', data: data })
}

// 删除订单
export function delOrder(ids) {
  return request({ url: '/manage/order/' + ids, method: 'delete' })
}

// 导出订单
export function exportOrder(data) {
  return request({ url: '/manage/order/export', method: 'post', data: data, responseType: 'blob' })
}
