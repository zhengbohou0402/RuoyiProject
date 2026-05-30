import request from '@/utils/request'

// 查询人员列表
export function listEmp(query) {
  return request({ url: '/manage/emp/list', method: 'get', params: query })
}

// 查询人员详细
export function getEmp(id) {
  return request({ url: '/manage/emp/' + id, method: 'get' })
}

// 新增人员
export function addEmp(data) {
  return request({ url: '/manage/emp', method: 'post', data: data })
}

// 修改人员
export function updateEmp(data) {
  return request({ url: '/manage/emp', method: 'put', data: data })
}

// 删除人员
export function delEmp(ids) {
  return request({ url: '/manage/emp/' + ids, method: 'delete' })
}

// 导出人员
export function exportEmp(data) {
  return request({ url: '/manage/emp/export', method: 'post', data: data, responseType: 'blob' })
}

// 根据设备获取运营人员列表
export function listBusinessEmp(innerCode) {
  return request({ url: '/manage/emp/businessList/' + innerCode, method: 'get' })
}

// 根据设备获取运维人员列表
export function listOperationEmp(innerCode) {
  return request({ url: '/manage/emp/operationList/' + innerCode, method: 'get' })
}
