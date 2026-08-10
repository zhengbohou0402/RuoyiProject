import request from '@/utils/request'

// 查询设备类型列表
export function listVmType(query) {
  return request({ url: '/manage/vmType/list', method: 'get', params: query })
}

// 查询设备类型详细
export function getVmType(id) {
  return request({ url: '/manage/vmType/' + id, method: 'get' })
}

// 新增设备类型
export function addVmType(data) {
  return request({ url: '/manage/vmType', method: 'post', data: data })
}

// 修改设备类型
export function updateVmType(data) {
  return request({ url: '/manage/vmType', method: 'put', data: data })
}

// 删除设备类型
export function delVmType(ids) {
  return request({ url: '/manage/vmType/' + ids, method: 'delete' })
}

// 导出设备类型
export function exportVmType(data) {
  return request({ url: '/manage/vmType/export', method: 'post', data: data, responseType: 'blob' })
}
