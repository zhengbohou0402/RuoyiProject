import request from '@/utils/request'

// 查询策略列表
export function listPolicy(query) {
  return request({ url: '/manage/policy/list', method: 'get', params: query })
}

// 查询策略详细
export function getPolicy(policyId) {
  return request({ url: '/manage/policy/' + policyId, method: 'get' })
}

// 新增策略
export function addPolicy(data) {
  return request({ url: '/manage/policy', method: 'post', data: data })
}

// 修改策略
export function updatePolicy(data) {
  return request({ url: '/manage/policy', method: 'put', data: data })
}

// 删除策略
export function delPolicy(policyIds) {
  return request({ url: '/manage/policy/' + policyIds, method: 'delete' })
}

// 导出策略
export function exportPolicy(data) {
  return request({ url: '/manage/policy/export', method: 'post', data: data, responseType: 'blob' })
}
