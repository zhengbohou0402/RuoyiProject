import request from '@/utils/request'

// 查询工单角色列表
export function listManageRole(query) {
  return request({ url: '/manage/role/list', method: 'get', params: query })
}

// 查询工单角色详细
export function getManageRole(roleId) {
  return request({ url: '/manage/role/' + roleId, method: 'get' })
}

// 新增工单角色
export function addManageRole(data) {
  return request({ url: '/manage/role', method: 'post', data: data })
}

// 修改工单角色
export function updateManageRole(data) {
  return request({ url: '/manage/role', method: 'put', data: data })
}

// 删除工单角色
export function delManageRole(roleIds) {
  return request({ url: '/manage/role/' + roleIds, method: 'delete' })
}

// 导出工单角色
export function exportManageRole(data) {
  return request({ url: '/manage/role/export', method: 'post', data: data, responseType: 'blob' })
}
