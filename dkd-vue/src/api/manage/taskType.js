import request from '@/utils/request'

// 查询工单类型列表
export function listTaskType(query) {
  return request({ url: '/manage/taskType/list', method: 'get', params: query })
}

// 查询工单类型详细
export function getTaskType(typeId) {
  return request({ url: '/manage/taskType/' + typeId, method: 'get' })
}

// 新增工单类型
export function addTaskType(data) {
  return request({ url: '/manage/taskType', method: 'post', data: data })
}

// 修改工单类型
export function updateTaskType(data) {
  return request({ url: '/manage/taskType', method: 'put', data: data })
}

// 删除工单类型
export function delTaskType(typeIds) {
  return request({ url: '/manage/taskType/' + typeIds, method: 'delete' })
}

// 导出工单类型
export function exportTaskType(data) {
  return request({ url: '/manage/taskType/export', method: 'post', data: data, responseType: 'blob' })
}
