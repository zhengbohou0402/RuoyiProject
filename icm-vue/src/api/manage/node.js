import request from '@/utils/request'

// 查询点位列表
export function listNode(query) {
  return request({ url: '/manage/node/list', method: 'get', params: query })
}

// 查询点位详细
export function getNode(id) {
  return request({ url: '/manage/node/' + id, method: 'get' })
}

// 新增点位
export function addNode(data) {
  return request({ url: '/manage/node', method: 'post', data: data })
}

// 修改点位
export function updateNode(data) {
  return request({ url: '/manage/node', method: 'put', data: data })
}

// 删除点位
export function delNode(ids) {
  return request({ url: '/manage/node/' + ids, method: 'delete' })
}

// 导出点位
export function exportNode(data) {
  return request({ url: '/manage/node/export', method: 'post', data: data, responseType: 'blob' })
}
