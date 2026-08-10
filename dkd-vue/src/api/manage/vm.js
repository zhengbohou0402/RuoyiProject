import request from '@/utils/request'

// 查询设备列表
export function listVm(query) {
  return request({ url: '/manage/vm/list', method: 'get', params: query })
}

// 查询设备详细
export function getVm(id) {
  return request({ url: '/manage/vm/' + id, method: 'get' })
}

// 新增设备
export function addVm(data) {
  return request({ url: '/manage/vm', method: 'post', data: data })
}

// 修改设备
export function updateVm(data) {
  return request({ url: '/manage/vm', method: 'put', data: data })
}

// 删除设备
export function delVm(ids) {
  return request({ url: '/manage/vm/' + ids, method: 'delete' })
}

// 导出设备
export function exportVm(data) {
  return request({ url: '/manage/vm/export', method: 'post', data: data, responseType: 'blob' })
}
